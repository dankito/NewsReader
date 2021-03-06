package net.dankito.newsreader.util.web;

import net.dankito.newsreader.util.web.callbacks.DownloadProgressListener;


public class RequestParameters {

  public static final int DEFAULT_DOWNLOAD_BUFFER_SIZE = 8 * 1024;


  protected String url;

  protected String body;

  protected ContentType contentType;

  protected String userAgent;

  protected CookieHandling cookieHandling = CookieHandling.ACCEPT_NONE;

  protected int connectionTimeoutMillis;

  protected int countConnectionRetries = 0;

  protected boolean hasStringResponse = true;

  protected int downloadBufferSize = DEFAULT_DOWNLOAD_BUFFER_SIZE;

  protected DownloadProgressListener downloadProgressListener;


  public RequestParameters(String url) {
    this.url = url;
    this.contentType = ContentType.FORM_URL_ENCODED;
  }

  public RequestParameters(String url, boolean hasStringResponse) {
    this(url);
    this.hasStringResponse = hasStringResponse;
  }

  public RequestParameters(String url, String body) {
    this(url);
    this.body = body;
  }


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isBodySet() {
    return isNotNullOrEmpty(getBody());
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public ContentType getContentType() {
    return contentType;
  }

  public void setContentType(ContentType contentType) {
    this.contentType = contentType;
  }

  public boolean isUserAgentSet() {
    return isNotNullOrEmpty(getUserAgent());
  }

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public CookieHandling getCookieHandling() {
    return cookieHandling;
  }

  public void setCookieHandling(CookieHandling cookieHandling) {
    this.cookieHandling = cookieHandling;
  }

  public boolean isConnectionTimeoutSet() {
    return getConnectionTimeoutMillis() > 0;
  }

  public int getConnectionTimeoutMillis() {
    return connectionTimeoutMillis;
  }

  public void setConnectionTimeoutMillis(int connectionTimeoutMillis) {
    this.connectionTimeoutMillis = connectionTimeoutMillis;
  }

  public boolean isCountConnectionRetriesSet() {
    return countConnectionRetries > 0;
  }

  public int getCountConnectionRetries() {
    return countConnectionRetries;
  }

  public void setCountConnectionRetries(int countConnectionRetries) {
    this.countConnectionRetries = countConnectionRetries;
  }

  public void decrementCountConnectionRetries() {
    this.countConnectionRetries--;
  }

  public boolean hasStringResponse() {
    return hasStringResponse;
  }

  public void setHasStringResponse(boolean hasStringResponse) {
    this.hasStringResponse = hasStringResponse;
  }

  public int getDownloadBufferSize() {
    return downloadBufferSize;
  }

  public void setDownloadBufferSize(int downloadBufferSize) {
    this.downloadBufferSize = downloadBufferSize;
  }

  public DownloadProgressListener getDownloadProgressListener() {
    return downloadProgressListener;
  }

  public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
    this.downloadProgressListener = downloadProgressListener;
  }


  private boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }

  private boolean isNotNullOrEmpty(String string) {
    return !isNullOrEmpty(string);
  }


  @Override
  public String toString() {
    return url;
  }

}
