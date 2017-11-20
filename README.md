# Camel 3scale Component

This component offers two Security Policies and an Endpoint for reporting metrics 
to the Red Hat 3scale API Management Platform.

The 3scale Component covers the Service Management API, which allows for 3 operations:
* Authorize : Read-only operation to authorize an application.
* AuthRep : A 'one-shot' operation to authorize an application and report 
the associated transaction at the same time.
* Report : Reports traffic for an application; typically used in OAuth 
where two calls are required: first authorize then report.

The security policies leverage the Authorize and AuthRep capabilities, while the 
component allows any Camel Route to report metrics to Red Hat 3scale API.