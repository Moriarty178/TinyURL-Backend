package tiny_url.app.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import tiny_url.app.backend.annotation.MyColumn;
import tiny_url.app.backend.annotation.MyEntity;
import tiny_url.app.backend.common.Constants;
import tiny_url.app.backend.common.Response;
import tiny_url.app.backend.common.ShortenResponse;
import tiny_url.app.backend.entity.UrlEntity;
import tiny_url.app.backend.service.UrlService;
import tiny_url.app.backend.utils.QRCodeGenerator;

import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UrlQueryController {
    @Value("${server.port}")
    private int port;

    private final UrlService urlService;

    public UrlQueryController(UrlService urlService) {
        this.urlService = urlService;
    }

    @QueryMapping
    public Response getParamList() throws Exception {
        List<Object> listParam  = new ArrayList<>();
        for (Field field : Constants.RESPONSE_CODE.class.getDeclaredFields()) {
            Object value = field.get(Constants.RESPONSE_CODE.class.getConstructor().newInstance());
            listParam.add(value);
        }

        return Response.success(Constants.RESPONSE_CODE.SUCCESS).withData(listParam);
    }

    @QueryMapping
    public String getLongUrl(@Argument String shortUrl) {
        return urlService.getLongUrl(shortUrl);
    }

    @QueryMapping
    public String getQRCode(@Argument String shortUrl) {
        try {
            DatagramSocket socket = new DatagramSocket();
            socket.connect(InetAddress.getByName("8.8.8.8"), 80);
            String ipV4 = socket.getLocalAddress().getHostAddress();
            String tinyUrl = "http://" + ipV4 + ":" + port + "/api/v1/tiny-url/" + shortUrl;
            return Base64.getEncoder().encodeToString(QRCodeGenerator.generatorQRCodeImage(tinyUrl, 250, 250));
        } catch (Exception e) {
            return null;
        }
    }

    @QueryMapping
    public List<ShortenResponse> getAllShortUrl() {
        List<UrlEntity> urlEntities = urlService.findAllUrl();

        return urlEntities.stream()
                .map(urlEntity -> new ShortenResponse(0, "Success", urlEntity.getShortUrl(), "status", LocalDate.now()))
                .collect(Collectors.toList());
    }

    @QueryMapping
    public List<String> getAllLongUrl() {
        return urlService.getAllLongUrl();
    }


}
