package gov.usgs.aqcu.retrieval;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import gov.usgs.aqcu.aquarius.AquariusAppsClient;

import gov.usgs.aqcu.exception.AquariusProcessingException;
import gov.usgs.aqcu.exception.AquariusRetrievalException;
import net.servicestack.client.IReturn;
import net.servicestack.client.WebServiceException;

import gov.usgs.aqcu.util.LogExecutionTime;

@Repository
public class AquariusAppsRetrievalService extends AquariusRetrievalService {
	private static final Logger LOG = LoggerFactory.getLogger(AquariusAppsRetrievalService.class);

        @LogExecutionTime
	protected <TResponse> TResponse executeAppsApiRequest(IReturn<TResponse> request) throws AquariusRetrievalException {
		String errorMessage = "";
		int unauthorizedRetryCounter = 0;
		boolean doRetry;

		do {
			doRetry = false;
			try {
				//Despite this being AutoCloseable we CANNOT close it or it will delete the session in AQ for our service account
				//and cause any other in-flight requests to fail.
                                LOG.debug("Authenticating service account against Aquarius.");
				AquariusAppsClient client = AquariusAppsClient.createConnectedClient(aquariusUrl.replace("/AQUARIUS/", ""), aquariusUser, aquariusPassword);
				return client.Apps.get(request);
			} catch (WebServiceException e) {
				if((isAuthError(e) || isInvalidTokenError(e)) && unauthorizedRetryCounter < aquariusUnauthorizedRetryCount) {
					doRetry = true;
					unauthorizedRetryCounter++;
					errorMessage = "Failed to get authorization for Aquarius Web Request " + request.toString() + 
					". Retrying (" + unauthorizedRetryCounter + " / " + aquariusUnauthorizedRetryCount + ")...";
					LOG.warn(errorMessage);
				} else {
					errorMessage = "A Web Service Exception occurred while executing an Apps V1 API Request against Aquarius:\n{" +
					"\nAquarius Instance: " + aquariusUrl +
					"\nRequest: " + request.toString() +
					"\nStatus: " + e.getStatusCode() + 
					"\nDescription: " + e.getStatusDescription() +
					"\nCause: " + e.getErrorMessage() +
					"\nDetails: " + e.getServerStackTrace() + "\n}\n";
					LOG.error(errorMessage);
					throw new AquariusRetrievalException(errorMessage);
				}
			} catch (Exception e) {
				LOG.error("An unexpected error occurred while attempting to fetch data from Aquarius: \n" +
					"Request: " + request.toString() + "\n Error: ", e);
				throw new AquariusProcessingException(e.getMessage());
			}
		} while(doRetry);

		LOG.error("Failed to retrieve data from Aquarius for request " + request.toString() + ". Out of retries.");
		throw new AquariusRetrievalException("Failed to retrieve data from Aquarius for request " + request.toString());
	}
}
