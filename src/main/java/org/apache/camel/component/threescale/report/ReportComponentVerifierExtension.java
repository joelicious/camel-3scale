package org.apache.camel.component.threescale.report;

import java.util.Map;

import org.apache.camel.component.extension.verifier.DefaultComponentVerifierExtension;
import org.apache.camel.component.extension.verifier.ResultBuilder;
import org.apache.camel.component.extension.verifier.ResultErrorBuilder;
import org.apache.camel.component.extension.verifier.ResultErrorHelper;
import org.apache.camel.component.threescale.ThreeScaleConfiguration;

import threescale.v3.api.ServiceApi;
import threescale.v3.api.impl.ServiceApiDriver;

public class ReportComponentVerifierExtension extends DefaultComponentVerifierExtension {

	private ServiceApi serviceApi;

	public ReportComponentVerifierExtension() {
		this("threescale-report");
	}

	public ReportComponentVerifierExtension(String scheme) {
		super(scheme);
	}

	// *********************************
	// Parameters validation
	// *********************************

	@Override
	protected Result verifyParameters(Map<String, Object> parameters) {

		ResultBuilder builder = ResultBuilder.withStatusAndScope(Result.Status.OK, Scope.PARAMETERS)
				.error(ResultErrorHelper.requiresOption("serverHost", parameters))
				.error(ResultErrorHelper.requiresOption("serverPort", parameters));

		// Validate using the catalog
		super.verifyParametersAgainstCatalog(builder, parameters);

		return builder.build();

	}

	// *********************************
	// Connectivity validation
	// *********************************

	@Override
	protected Result verifyConnectivity(Map<String, Object> parameters) {
		ResultBuilder builder = ResultBuilder.withStatusAndScope(Result.Status.OK, Scope.CONNECTIVITY);

		try {
			ThreeScaleConfiguration configuration = setProperties(new ThreeScaleConfiguration(), parameters);
			serviceApi = ServiceApiDriver.createApi(configuration.getServerHost(), configuration.getServerPort(), true);

			// TODO
			// Do I need to authorize here?
			
		} catch (Exception e) {
			builder.error(ResultErrorBuilder.withException(e).build());
		}

		return builder.build();
	}

}