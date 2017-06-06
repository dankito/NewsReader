package net.dankito.newsreader.util.web.callbacks;


public interface DownloadProgressListener {

  void progress(float progress, byte[] downloadedChunk);

}
