package net.dankito.newsreader.util.web.responses;


public class ResponseBase {

  protected boolean isSuccessful;

  protected String error;


  public ResponseBase(boolean isSuccessful) {
    this.isSuccessful = isSuccessful;
  }

  public ResponseBase(String error) {
    this(false);
    this.error = error;
  }


  public boolean isSuccessful() {
    return isSuccessful;
  }

  public String getError() {
    return error;
  }


  @Override
  public String toString() {
    String description = "Is Successful ? " + isSuccessful();

    if(getError() != null) {
      description += "; Error: " + getError();
    }

    return description;
  }

}
