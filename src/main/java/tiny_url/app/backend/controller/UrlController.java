package tiny_url.app.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tiny_url.app.backend.common.Constants;
import tiny_url.app.backend.common.Response;
import tiny_url.app.backend.service.UrlService;
import tiny_url.app.backend.utils.QRCodeGenerator;

import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/v1/tiny-url")
public class UrlController {

    @Value("${server.port}")
    private int port;

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    // API tạo shortURL
    @PostMapping("/shorten")
    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> request) {
        String longUrl = request.get("long_url");
        if (longUrl == null || longUrl.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("Error", "Invalid URL"));
        } else {
            String shortUrl = urlService.shortenUrl(longUrl);
            return ResponseEntity.ok(Map.of("EC", 0, "MS", "Success", "shortUrl", shortUrl));
        }
    }

    @PostMapping(path = "/shorten1")
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody Response shortenUrl1(@RequestBody Map<String, String> request) {
        String longUrl = request.get("long_url");
        if (longUrl == null || longUrl.isBlank()) {
            return Response.warning(Constants.RESPONSE_CODE.NOT_FOUND);
        } else {
            String shortUrl = urlService.shortenUrl(longUrl);
            return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(shortUrl);
        }
    }

    // lấy toàn bộ giá trị của các thuộc tính của 1 class
    @GetMapping(path = "/get-param")
    public @ResponseBody Response getParamList() throws Exception {
        List<Object> listParam = new ArrayList<>();
        System.out.println("ALL fields: " + Constants.RESPONSE_CODE.class.getDeclaredFields());
        for (Field field : Constants.RESPONSE_CODE.class.getDeclaredFields()) {
            System.out.println("Field = " + field);
            Class<?> type = field.getType();
            Object objectValue = type.newInstance();
            Object value = field.get(objectValue); // do ko biết type, chứ nếu biết vd nó là String thì: value = field.get(String.class.newInstant());
            listParam.add(value);
        }

        return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(listParam);
    }

    // API get shortURL đã tạo
    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> getShortUrl(@PathVariable String shortUrl, HttpServletRequest request) {
        String longUrl = urlService.getLongUrl(shortUrl);
        if (longUrl != null) {
            // ghi log click
            //urlService.logClick(shortUrl, request);

            // Điều hướng đến đường dẫn gốc (longUrl)
            return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
                    .header("Location", longUrl)
                    .build();
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/qrcode")
    public ResponseEntity<?> getQRCode(@RequestParam(value = "shortUrl") String shortUrl) {
        try {
            // Lay ip v4 cho thiet bi khac tren ket noi trong cung mang noi bo
            DatagramSocket socket =  new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 80);
            String ipV4 = socket.getLocalAddress().getHostAddress();//Inet4Address.getLocalHost().getHostAddress();

            String tinyUrl = "http://" + ipV4 + ":" + port + "/api/v1/tiny-url/" + shortUrl;

            byte[] qrCode = QRCodeGenerator.generatorQRCodeImage(tinyUrl, 250, 250);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
