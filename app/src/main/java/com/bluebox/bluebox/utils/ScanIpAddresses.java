package com.bluebox.bluebox.utils;

import android.view.View;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ScanIpAddresses {

//    public static void test(View view, Requests.Callback<String> cb) {
//        Logger.d("Scanning all ip addresses...");
//        view.postDelayed(() -> {
//            String ip = scanIpAddresses();
//            if (ip == null) {
//                test(view, cb);
//            } else {
//                cb.returnResponse(ip);
//            }
//        }, 5000);
//    }

    /**
     * from https://stackoverflow.com/questions/11547082/fastest-way-to-scan-ports-with-java/11547117#11547117
     * @return (String) ip address if found, else null
     */
    public static String scanIpAddresses() {
        ExecutorService es = Executors.newFixedThreadPool(20);
        String ipPrefix = "192.168.43.";
        String ip;
        int port = 12345;
        int timeout = 200;

        List<Future<ScanResult>> futures = new ArrayList<>();
        for (int digit = 1; digit <= 255; digit++) {
            ip = ipPrefix + digit;
            futures.add(ipAddressExists(es, ip, port, timeout));
        }

        try {
            es.awaitTermination(200L, TimeUnit.MILLISECONDS);

            for (Future<ScanResult> f : futures) {
                if (f.get().isOpen) {
                    return f.get().ip;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            Logger.e("Exception " + e);
        }

        return null;
    }

    public static Future<ScanResult> ipAddressExists(ExecutorService es, String ip, int port, int timeout) {
        return es.submit(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, port), timeout);
                socket.close();
                return new ScanResult(ip, true);
            } catch (Exception ex) {
                return new ScanResult(ip, false);
            }
        });
    }

    private static class ScanResult {
        public String ip;
        public boolean isOpen;

        public ScanResult(String ip, boolean isOpen) {
            this.ip = ip;
            this.isOpen = isOpen;
        }
    }
}
