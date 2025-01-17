package org.signal.core.util.logging;

import android.annotation.SuppressLint;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import org.signal.core.util.BuildConfig;

@SuppressLint("LogNotSignal")
public final class Log {

  /**
   * @param internalCheck A checker that will indicate if this is an internal user
   * @param loggers A list of loggers that will be given every log statement.
   */
  @MainThread
  public static void initialize(@NonNull InternalCheck internalCheck, Logger... loggers) {
    LogManager.setInternalCheck(internalCheck);
    LogManager.setLoggers(loggers);
  }

  public static void initialize(Logger... loggers) {
    LogManager.setLoggers(loggers);
  }

  private static String redact(final String message) {
    if (message == null) {
      return null;
    }
    if (BuildConfig.DEBUG || message.isEmpty()) {
      return message;
    }
    return Scrubber.scrub(message).toString();
  }

  public static void v(String tag, String message) {
    v(tag, message, null);
  }

  public static void d(String tag, String message) {
    d(tag, message, null);
  }

  public static void i(String tag, String message) {
    i(tag, message, null);
  }

  public static void w(String tag, String message) {
    w(tag, message, null);
  }

  public static void e(String tag, String message) {
    e(tag, message, null);
  }

  public static void wtf(String tag, String message) {
    wtf(tag, message, null);
  }

  public static void v(String tag, Throwable t) {
    v(tag, null, t);
  }

  public static void d(String tag, Throwable t) {
    d(tag, null, t);
  }

  public static void i(String tag, Throwable t) {
    i(tag, null, t);
  }

  public static void w(String tag, Throwable t) {
    w(tag, null, t);
  }

  public static void e(String tag, Throwable t) {
    e(tag, null, t);
  }

  public static void wtf(String tag, Throwable t) {
    wtf(tag, null, t);
  }

  public static void v(String tag, String message, Throwable t) {
    LogManager.getLogger().v(tag, redact(message), t);
  }

  public static void d(String tag, String message, Throwable t) {
    LogManager.getLogger().d(tag, redact(message), t);
  }

  public static void i(String tag, String message, Throwable t) {
    LogManager.getLogger().i(tag, redact(message), t);
  }

  public static void w(String tag, String message, Throwable t) {
    LogManager.getLogger().w(tag, redact(message), t);
  }

  public static void e(String tag, String message, Throwable t) {
    LogManager.getLogger().e(tag, redact(message), t);
  }

  public static void wtf(String tag, String message, Throwable t) {
    LogManager.getLogger().wtf(tag, redact(message), t);
  }

  public static String tag(Class<?> clazz) {
    String simpleName = clazz.getSimpleName();
    if (simpleName.length() > 23) {
      return simpleName.substring(0, 23);
    }
    return simpleName;
  }

  /**
   * Important: This is not something that can be used to log PII. Instead, it's intended use is for
   * logs that might be too verbose or otherwise unnecessary for public users.
   *
   * @return The normal logger if this is an internal user, or a no-op logger if it isn't.
   */
  public static Logger internal() {
    return LogManager.getInternal();
  }

  public static void blockUntilAllWritesFinished() {
    LogManager.getLogger().blockUntilAllWritesFinished();
  }

  public static abstract class Logger {
    public abstract void v(String tag, String message, Throwable t);
    public abstract void d(String tag, String message, Throwable t);
    public abstract void i(String tag, String message, Throwable t);
    public abstract void w(String tag, String message, Throwable t);
    public abstract void e(String tag, String message, Throwable t);
    public abstract void wtf(String tag, String message, Throwable t);
    public abstract void blockUntilAllWritesFinished();
    public abstract void clear();

    public void v(String tag, String message) {
      v(tag, message, null);
    }

    public void d(String tag, String message) {
      d(tag, message, null);
    }

    public void i(String tag, String message) {
      i(tag, message, null);
    }

    public void w(String tag, String message) {
      w(tag, message, null);
    }

    public void e(String tag, String message) {
      e(tag, message, null);
    }

    public void wtf(String tag, String message) {
      wtf(tag, message, null);
    }
  }

  public interface InternalCheck {
    boolean isInternal();
  }
}
