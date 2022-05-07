package com.adevguide.java.simpleproject;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {
    private static final Logger logger = Logger.getLogger("SimpleProject");
    private static final String WINDOWS_COMMAND = "powershell.exe";
    private static Boolean cachedResult = null;

    public static void main(String[] args) {
    System.out.println("\n\n***************************************\n\n");
    System.out.println("This Application is build using Docker.");
    System.out.println("Type Something and it will be displayed on the console.");
    System.out.println(isRunningOnGcp());
    System.out.println("\n\nOk Bye********************************\n\n");
    }



private static String isRunningOnGcp() {
    String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
    try {
      if (osName.startsWith("linux")) {
        // Checks GCE residency on Linux platform.
        if (checkProductNameOnLinux(
            Files.newBufferedReader(Paths.get("/sys/class/dmi/id/product_name"), UTF_8))) {
                return "GCE Linux";
            }
        else {
            return "Non GCE Linux";
        }    
      } else if (osName.startsWith("windows")) {
        // Checks GCE residency on Windows platform.
        Process p =
            new ProcessBuilder()
                .command(WINDOWS_COMMAND, "Get-WmiObject", "-Class", "Win32_BIOS")
                .start();
        if (checkBiosDataOnWindows(
            new BufferedReader(new InputStreamReader(p.getInputStream(), UTF_8)))) {
                return "GCE Windows";
            }
        else {
            return "Non GCE Windows";
        }
      }
    } catch (Exception e) {
      logger.log(Level.WARNING, "Fail to read platform information: ", e);
      return "GCE detection error: " + e;
    }
    // Platforms other than Linux and Windows are not supported.
    return "unsupported platform";
  }

  static boolean checkProductNameOnLinux(BufferedReader reader) throws IOException {
    String name = reader.readLine().trim();
    return name.equals("Google") || name.equals("Google Compute Engine");
  }

  static boolean checkBiosDataOnWindows(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      if (line.startsWith("Manufacturer")) {
        String name = line.substring(line.indexOf(':') + 1).trim();
        return name.equals("Google");
      }
    }
    return false;
  }

}