package org.apache.camel.component.threescale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import threescale.v3.api.AuthorizeResponse;
import threescale.v3.api.HttpResponse;
import threescale.v3.api.ParameterMap;
import threescale.v3.api.ReportResponse;
import threescale.v3.api.ServerError;
import threescale.v3.api.impl.ServiceApiDriver;

public class ServiceApiDriverMock extends ServiceApiDriver {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceApiDriverMock.class);

	public static boolean return200 = true;

	@Override
	public AuthorizeResponse authorize(ParameterMap parameters) throws ServerError {
		LOG.info("Mock Authorize::parameterMap");
		
		final String body = "<status>" +
                "<authorized>true</authorized>" +
                "<plan>Ultimate</plan>" +
                "<usage_reports>" +
                "    <usage_report metric=\"hits\" period=\"day\">" +
                "      <period_start>2010-04-26 00:00:00 +0000</period_start>" +
                "      <period_end>2010-04-27 00:00:00 +0000</period_end>" +
                "      <current_value>10023</current_value>" +
                "      <max_value>50000</max_value>" +
                "    </usage_report>" +

                "    <usage_report metric=\"hits\" period=\"month\">" +
                "      <period_start>2010-04-01 00:00:00 +0000</period_start>" +
                "      <period_end>2010-05-01 00:00:00 +0000</period_end>" +
                "      <current_value>999872</current_value>" +
                "      <max_value>150000</max_value>" +
                "    </usage_report>" +
                "  </usage_reports>" +
                "</status>";
		
		final String appKeysSuccess = "<status>" +
                "<authorized>true</authorized>" +
                "<plan>Ultimate</plan>" +
                "</status>";
		
        final String invalidAppId = "<error code=\"application_not_found\">application with id=\"foo\" was not found</error>";



		AuthorizeResponse authorizeResponse;
		
		if (true == return200) {
			authorizeResponse = new AuthorizeResponse(200, appKeysSuccess);
		} else {
			authorizeResponse = new AuthorizeResponse(503, invalidAppId);
		}

		return authorizeResponse;

	}

	@Override
	public AuthorizeResponse authorize(String serviceToken, String serviceId, ParameterMap parameters)
			throws ServerError {
		LOG.info("Mock Authorize");

		AuthorizeResponse authorizeResponse;
		String httpContent = "";
		
		if (true == return200) {
			authorizeResponse = new AuthorizeResponse(200, httpContent);
		} else {
			authorizeResponse = new AuthorizeResponse(503, httpContent);
		}
		
		return authorizeResponse;
	}

	@Override
	public AuthorizeResponse authrep(ParameterMap metrics) throws ServerError {
		LOG.info("Mock AuthRep::ParameterMap");

		AuthorizeResponse authorizeResponse;
		String httpContent = "";

		if (true == return200) {
			authorizeResponse = new AuthorizeResponse(200, httpContent);
		} else {
			authorizeResponse = new AuthorizeResponse(503, httpContent);
		}

		return authorizeResponse;
	}

	@Override
	public AuthorizeResponse authrep(String serviceToken, String serviceId, ParameterMap metrics) throws ServerError {
		LOG.info("Mock AuthRep");

		AuthorizeResponse authorizeResponse;
		String httpContent = "";

		if (true == return200) {
			authorizeResponse = new AuthorizeResponse(200, httpContent);
		} else {
			authorizeResponse = new AuthorizeResponse(503, httpContent);
		}

		return authorizeResponse;

	}

	@Override
	public ReportResponse report(String serviceToken, String serviceId, ParameterMap... transactions)
			throws ServerError {
		LOG.info("Mock Rep::serviceToken");

		String httpBody = "";
		String httpBadResponse = "<error code=\"provider_key_invalid\">provider key \"foo\" is invalid</error>";
		ReportResponse reportResponse;
		HttpResponse http200Response = new HttpResponse(200, httpBody);
		HttpResponse http503Response = new HttpResponse(503, httpBadResponse);
		
		if (true == return200) {
			reportResponse = new ReportResponse(http200Response);
		} else {
			reportResponse = new ReportResponse(http503Response);
		}
		
		return reportResponse;

	}

	@Override
	public ReportResponse report(String serviceId, ParameterMap... transactions) throws ServerError {
		LOG.info("Mock Rep");

		String httpBody = "";
		String httpBadResponse = "<error code=\"provider_key_invalid\">provider key \"foo\" is invalid</error>";
		ReportResponse reportResponse;
		HttpResponse http200Response = new HttpResponse(200, httpBody);
		HttpResponse http503Response = new HttpResponse(503, httpBadResponse);
		
		if (true == return200) {
			reportResponse = new ReportResponse(http200Response);
		} else {
			reportResponse = new ReportResponse(http503Response);
		}

		return reportResponse;
	}

	@Override
	public AuthorizeResponse oauth_authorize(ParameterMap params) throws ServerError {

		LOG.info("Mock OAuth Authorize:ParameterMap Not Supported");
		throw new ServerError("Not Supported");

	}

	@Override
	public AuthorizeResponse oauth_authorize(String serviceToken, String serviceId, ParameterMap params)
			throws ServerError {

		LOG.info("Mock OAuth Authorize Not Supported");
		throw new ServerError("Not Supported");

	}
}