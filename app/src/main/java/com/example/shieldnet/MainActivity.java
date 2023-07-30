package com.example.shieldnet;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//Menu
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

//Speed Check
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import android.os.AsyncTask;

//Password
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import java.util.regex.Pattern;

//Packet Sniffing
/*
import org.pcap4j.core.BpfProgram;
import org.pcap4j.core.PacketListener;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.packet.Packet;
*/






public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView speedTextView;
    private EditText passwordEditText;
    private TextView safetyLevelTextView;
    private TextView resultTextView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinner = findViewById(R.id.spinner101);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Features, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        System.out.println("run");


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        Spinner spinner = findViewById(R.id.spinner101);
        String text = parent.getItemAtPosition(i).toString();
        //PasswordChecker stuff
        passwordEditText = findViewById(R.id.passwordEditText);
        safetyLevelTextView = findViewById(R.id.safetyLevelTextView);
        Button checkButton = findViewById(R.id.checkButton);

        //Latency Checker
        speedTextView = findViewById(R.id.speedTextView);

        //Packet Scanner stuff
        resultTextView = findViewById(R.id.resultTextView);
        Button startScanButton = findViewById(R.id.startScanButton);

        if(i == 0){
            passwordEditText.setVisibility(View.VISIBLE);
            safetyLevelTextView.setVisibility(View.VISIBLE);
            checkButton.setVisibility(View.VISIBLE);

            speedTextView.setVisibility(View.INVISIBLE);
            resultTextView.setVisibility(View.INVISIBLE);
            startScanButton.setVisibility(View.INVISIBLE);

            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPasswordSafety();
                }
            });
        }

        else if(i == 1){
            passwordEditText.setVisibility(View.INVISIBLE);
            safetyLevelTextView.setVisibility(View.INVISIBLE);
            checkButton.setVisibility(View.INVISIBLE);
            resultTextView.setVisibility(View.INVISIBLE);
            startScanButton.setVisibility(View.INVISIBLE);

            speedTextView.setVisibility(View.VISIBLE);
            new NetworkSpeedCheckerTask().execute();
        }
        else if(i == 2){
            passwordEditText.setVisibility(View.INVISIBLE);
            safetyLevelTextView.setVisibility(View.INVISIBLE);
            checkButton.setVisibility(View.INVISIBLE);
            speedTextView.setVisibility(View.INVISIBLE);

            resultTextView.setVisibility(View.VISIBLE);
            startScanButton.setVisibility(View.VISIBLE);

            startScanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resultTextView.setText("No Current Threats!");
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    // Network Speed Functions
    private class NetworkSpeedCheckerTask extends AsyncTask<Void, Void, Double> {
        @Override
        protected Double doInBackground(Void... voids) {
            return getNetworkSpeedMbps();
        }
        @Override
        protected void onPostExecute(Double speedMbps) {
            if (speedMbps != -1) {
                speedTextView.setText("Network Speed: " + speedMbps + " Mbps");
            } else {
                speedTextView.setText("Failed to calculate network speed.");
            }
        }
    }
    private double getNetworkSpeedMbps() {
        String fileUrl = "http://speedtest.tele2.net/10MB.zip"; // URL of the file to download (replace with any large file)
        int fileSizeBytes = 10 * 1024 * 1024; // 10 MB (size of the file in bytes)

        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            long startTime = System.currentTimeMillis();

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream())) {
                byte[] data = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = in.read(data, 0, 1024)) != -1) {
                    totalBytesRead += bytesRead;
                    if (totalBytesRead >= fileSizeBytes) {
                        break;
                    }
                }

                long endTime = System.currentTimeMillis();
                long downloadTimeMs = endTime - startTime;

                // Calculate speed in Mbps
                double speedMbps = ((double) fileSizeBytes * 8) / (downloadTimeMs / 1000.0) / 100000;

                return speedMbps;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if the speed calculation fails
    } //Network Speed Checker


    //Password Safety Functions
    private void checkPasswordSafety() {
        String password = passwordEditText.getText().toString();

        // Call the function to evaluate password safety
        int safetyLevel = evaluatePasswordSafety(password);

        // Set the safety level text on the TextView
        String safetyLevelMessage = getSafetyLevelMessage(safetyLevel);
        safetyLevelTextView.setText("Password Safety Level: " + safetyLevelMessage);
    }

    private int evaluatePasswordSafety(String password) {
        int safetyLevel = 0;

        // Check password length
        if (password.length() >= 8) {
            safetyLevel++;
        }

        // Check uppercase characters
        if (Pattern.compile("[A-Z]").matcher(password).find()) {
            safetyLevel++;
        }

        // Check lowercase characters
        if (Pattern.compile("[a-z]").matcher(password).find()) {
            safetyLevel++;
        }

        // Check digits
        if (Pattern.compile("\\d").matcher(password).find()) {
            safetyLevel++;
        }

        // Check special characters
        if (Pattern.compile("[^a-zA-Z\\d]").matcher(password).find()) {
            safetyLevel++;
        }
        return safetyLevel;
    }
    private String getSafetyLevelMessage(int safetyLevel) {
        // Add your safety level messages here as shown in the previous example
        switch (safetyLevel) {
            case 0:
                return "Very Weak";
            case 1:
                return "Weak";
            case 2:
                return "Moderate";
            case 3:
                return "Strong";
            case 4:
                return "Very Strong";
            default:
                return "Unknown";
        }
    }

    /*
    private class PacketScannerTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                PcapNetworkInterface networkInterface = PcapNetworkInterface.getDevByName("wlan0"); // Change this to your network interface

                // Open the network interface in promiscuous mode with a large snapshot length.
                PcapHandle handle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 20);

                // Set a BPF filter to capture only TCP and UDP packets (you can modify the filter as needed).
                handle.setFilter("tcp or udp", BpfProgram.BpfCompileMode.OPTIMIZE);

                // Create a packet listener to handle incoming packets.
                PacketListener packetListener = new PacketListener() {
                    @Override
                    public void gotPacket(Packet packet) {
                        boolean isSuspicious = checkPacket(packet);

                        if (isSuspicious) {
                            // Process suspicious packet (e.g., log, display alert, etc.).
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    resultTextView.setText("Suspicious packet detected: " + packet.toString());
                                }
                            });
                        }
                    }
                };

                // Start capturing packets asynchronously.
                handle.loopPacket(-1, packetListener);

                // Close the handle when done.
                handle.close();
            } catch (PcapNativeException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isSuccessful) {
            if (isSuccessful) {
                resultTextView.setText("Packet scanning completed.");
            } else {
                resultTextView.setText("Failed to start packet scanning.");
            }

            isScanning = false;
        }
    }

    private boolean checkPacket(Packet packet) {
        // Add your packet inspection logic here to detect suspicious packets.
        // For example, you can check for specific flags in TCP packets,
        // abnormal packet sizes, unusual source/destination IP addresses, etc.

        // Replace this placeholder with your actual threat detection logic.
        return false;
    }
*/

}









