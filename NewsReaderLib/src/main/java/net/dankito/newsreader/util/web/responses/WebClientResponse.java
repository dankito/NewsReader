package net.dankito.newsreader.util.web.responses;


public class WebClientResponse extends ResponseBase {

  protected String body;


  public WebClientResponse(String error) {
    super(error);
  }

  public WebClientResponse(boolean isSuccessful) {
    super(isSuccessful);
  }

  public WebClientResponse(boolean successful, String body) {
    super(successful);

    this.body = body;
  }


  public String getBody() {
    return body;
  }

}
