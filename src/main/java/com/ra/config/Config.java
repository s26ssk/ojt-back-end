package com.ra.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.ra.dto.response.BingMapLocationResponse;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Config {public static class FailCause {
    public static final String LD1 = "Khách hàng vắng, hẹn giao sau";
    public static final String LD2 = "Không liên hệ được với khách hàng";
    public static final String LD3 = "Từ chối nhận vì hàng không như mô tả";
    public static final String LD4 = "Từ chối nhận vì kiện hàng rách/móp";
    public static final String LD5 = "Lý do khác";

}
    public static final String DISTANCE_URL = "https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix?" +
            "key=AhGt2ToN6G7LgofUZhnoQUlcnX91r0SXXLt2-gOntSQpo8ULuVgFHoyuitycbsnU" +
            "&travelMode=driving";
    public static final String LOCATION_URL = "https://dev.virtualearth.net/REST/v1/Locations?" +
            "key=AhGt2ToN6G7LgofUZhnoQUlcnX91r0SXXLt2-gOntSQpo8ULuVgFHoyuitycbsnU" +
            "&query=";
    public static final HttpClient client = HttpClient.newHttpClient();
    public static final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static final String[] ALPHABET = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    public static final int COLUMN_PROVIDER_NAME = 1;
    public static final int COLUMN_PROVIDER_PHONE = 2;
    public static final int COLUMN_PROVIDER_EMAIL = 3;
    public static final int COLUMN_PROVIDER_ADDRESS = 4;
    public static final int COLUMN_RECEIVER_NAME = 5;
    public static final int COLUMN_RECEIVER_PHONE = 6;
    public static final int COLUMN_RECEIVER_EMAIL = 7;
    public static final int COLUMN_RECEIVER_ADDRESS = 8;

    public static Date date() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static BufferedImage generateBarcodeImage(String barcodeText) throws Exception {
        Code128Writer barcodeWriter = new Code128Writer();
        BitMatrix bitMatrix = barcodeWriter.encode(barcodeText, BarcodeFormat.CODE_128, 300, 150);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        QRCodeWriter barcodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix =
                barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }

    public static Map<String, Double> getCoordinates(String address) {

        try {
            String encodeAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(LOCATION_URL + encodeAddress)).build();
            HttpResponse<String> response = Config.client.send(request, HttpResponse.BodyHandlers.ofString());
            String jsonResponse = response.body();
            BingMapLocationResponse locationResponse = Config.objectMapper.readValue(jsonResponse, BingMapLocationResponse.class);
            return getStringDoubleMap(locationResponse);
        } catch (IOException | InterruptedException e) {
            return null;
        }

    }

    private static Map<String, Double> getStringDoubleMap(BingMapLocationResponse locationResponse) {
        Map<String, Double> resultMap = new HashMap<>();
        if (locationResponse.getResourceSets().isEmpty()) {
            return null;
        }
        if (locationResponse.getResourceSets().get(0).getEstimatedTotal() < 1) {
            return null;
        }
        BingMapLocationResponse.Point point = locationResponse.getResourceSets().get(0).getResources().get(0).getPoint();
        resultMap.put("latitude", point.getCoordinates().get(0));
        resultMap.put("longitude", point.getCoordinates().get(1));
        return resultMap;
    }
}