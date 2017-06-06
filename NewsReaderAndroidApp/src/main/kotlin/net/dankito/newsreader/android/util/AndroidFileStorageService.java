package net.dankito.newsreader.android.util;

import android.content.Context;

import net.dankito.newsreader.util.JavaFileStorageService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;


public class AndroidFileStorageService extends JavaFileStorageService {

  protected Context context;


  public AndroidFileStorageService(Context context) {
    this.context = context;
  }


  @Override
  public File getFileInDataFolder(String filename) {
    return new File(context.getFilesDir(), filename);
  }


  @Override
  public OutputStream createFileOutputStream(String filename) throws FileNotFoundException {
    return context.openFileOutput(filename, Context.MODE_PRIVATE);
  }

  @Override
  public InputStream createFileInputStream(String filename) throws FileNotFoundException {
    return context.openFileInput(filename);
  }

}
