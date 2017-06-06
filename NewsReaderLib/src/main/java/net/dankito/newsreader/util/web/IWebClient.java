package net.dankito.newsreader.util.web;

import net.dankito.newsreader.util.web.callbacks.RequestCallback;
import net.dankito.newsreader.util.web.responses.WebClientResponse;


public interface IWebClient {

  WebClientResponse get(RequestParameters parameters);
  void getAsync(RequestParameters parameters, final RequestCallback callback);

  WebClientResponse post(RequestParameters parameters);
  void postAsync(RequestParameters parameters, final RequestCallback callback);

}
