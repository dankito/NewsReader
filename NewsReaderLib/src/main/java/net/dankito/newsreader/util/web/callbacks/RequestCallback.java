package net.dankito.newsreader.util.web.callbacks;

import net.dankito.newsreader.util.web.responses.WebClientResponse;


public interface RequestCallback {

  void completed(WebClientResponse response);

}
