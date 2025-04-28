package tiny_url.app.backend.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import tiny_url.app.backend.common.Constants;
import tiny_url.app.backend.common.Response;
import tiny_url.app.backend.common.ShortenResponse;
import tiny_url.app.backend.service.UrlService;

@Controller
public class UrlMutationController {

    private final UrlService urlService;

    public UrlMutationController(UrlService urlService) {
        this.urlService = urlService;
    }

    @MutationMapping
    public ShortenResponse shortenUrl(@Argument String longUrl) {
        if(longUrl == null || longUrl.isBlank()) {
            return new ShortenResponse(1, "Invalid URL", null);
        } else {
            String tinyUrl = urlService.shortenUrl(longUrl);
            return new ShortenResponse(0, "Success", tinyUrl);
        }
    }

    @MutationMapping
    public Response shortenUrl1(@Argument String longUrl) {
        if (longUrl == null || longUrl.isBlank()) {
            return new Response(Constants.RESPONSE_CODE.WARNING, Constants.RESPONSE_TYPE.WARNING, "Invalid", null);
        } else {
            String tinyUrl = urlService.shortenUrl(longUrl);
            return new Response(Constants.RESPONSE_CODE.SUCCESS, Constants.RESPONSE_TYPE.SUCCESS, "Success", tinyUrl);
        }
    }
}
