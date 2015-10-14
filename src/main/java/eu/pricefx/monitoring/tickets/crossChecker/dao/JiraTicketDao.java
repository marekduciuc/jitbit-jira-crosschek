package eu.pricefx.monitoring.tickets.crossChecker.dao;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.util.concurrent.Promise;

import eu.pricefx.monitoring.tickets.crossChecker.configuration.Configuartion;

public class JiraTicketDao {

  final HttpHeaders headers = new HttpHeaders();
  final RestTemplate restTemplate = new RestTemplate();
  final URI uri;
  final Configuartion configuartion;
  final JiraRestClientFactory restClientFactory;
  final BasicHttpAuthenticationHandler myHandler;

  public JiraTicketDao(Configuartion config) {
    configuartion = config;
    this.uri = URI.create(configuartion.getJiraBaseUrl());
    restClientFactory = new AsynchronousJiraRestClientFactory();
    myHandler = new BasicHttpAuthenticationHandler(configuartion.getJiraUser(),
        configuartion.getJiraPassword());
  }

  public String getIssuState(String id) {
    // * removing blans form issue id
    Issue issue = null;
    try {
      final JiraRestClient restClient = restClientFactory.create(uri, myHandler);
      final IssueRestClient issuclient = restClient.getIssueClient();
      issue = issuclient.getIssue(id).claim();
      restClient.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      if (issue != null) {
        return issue.getStatus().getName();
      } else {
        return "";
      }
    }
  }

}
