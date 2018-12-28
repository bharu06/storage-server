package com.documentviewer.docviewer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
public class DocumentsController {


    ObjectMapper objectMapper = new ObjectMapper();
    @Value("${key}")
    private String key;

    @Value("${serverip}")
    private String serverip;



    @RequestMapping("/files")
    public String files(@RequestParam final String path,
                        HttpServletResponse servletResponse) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "User R3TZtW2uIQ6LyejWZREefYP3eqejEcUd9nn8ObJOsPg=, Organization 57b84be33de31c80f177a9b8e7d3c7d3, Element SwPRqYDKi4TNmDdu645dm7SL1iDFf1MwtKRM+j8RZRs=");
        HttpEntity<String> entityReq = new HttpEntity<String>("", headers);

        Map<String, String> params = new HashMap<String, String>();
        params.put("path", path);
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://staging.cloud-elements.com/elements/api-v2/files?path={path}";

        ResponseEntity<String> responseObj = restTemplate.exchange(url, HttpMethod.GET,entityReq, String.class, params);
        System.out.println("Status code:" + responseObj.getStatusCode());
        servletResponse.setHeader(HttpHeaders.CONTENT_TYPE, responseObj.getHeaders().get(HttpHeaders.CONTENT_TYPE).get(0));
        servletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                responseObj.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));
        return responseObj.getBody();
    }

    /**
     * POST on /files.
     * @param path
     * @param file
     * @param servletResponse
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public String addfile(@RequestParam final String path,
                          @RequestParam("file") MultipartFile file,
                          HttpServletResponse servletResponse) throws Exception {

        System.out.println("Add file called");

        String uri = "https://staging.cloud-elements.com/elements/api-v2/files?path=" + path + "/" + file.getOriginalFilename();
        System.out.println("Uploading to " + uri);

        ByteArrayResource fileAsResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }

            @Override
            public long contentLength() {
                return file.getSize();
            }
        };
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
        parts.add("file", fileAsResource);
        parts.add("fileName", file.getOriginalFilename());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "User R3TZtW2uIQ6LyejWZREefYP3eqejEcUd9nn8ObJOsPg=, Organization 57b84be33de31c80f177a9b8e7d3c7d3, Element SwPRqYDKi4TNmDdu645dm7SL1iDFf1MwtKRM+j8RZRs=");
        httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<MultiValueMap<String, Object>>(parts, httpHeaders);


        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            System.out.println(responseEntity.getStatusCode());
            servletResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            servletResponse.setHeader("Location", "http://"+serverip+ "/?path="+path);
            return "";
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.CONFLICT) {
                return "File with path=" + path + " and name=" + file.getOriginalFilename() + " name exists";
            }
            servletResponse.sendError(500, exception.getMessage());
            return "";
        }
    }
    @CrossOrigin
    @RequestMapping("/contents")
    public String contents(@RequestParam final String path,
                        HttpServletResponse servletResponse) throws Exception {
        ObjectNode node = objectMapper.createObjectNode();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "User R3TZtW2uIQ6LyejWZREefYP3eqejEcUd9nn8ObJOsPg=, Organization 57b84be33de31c80f177a9b8e7d3c7d3, Element SwPRqYDKi4TNmDdu645dm7SL1iDFf1MwtKRM+j8RZRs=");
        HttpEntity<String> entityReq = new HttpEntity<String>("", headers);
        Map<String, String> params = new HashMap<String, String>();
        params.put("path", path);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://staging.cloud-elements.com/elements/api-v2/folders/contents?path={path}";
        ResponseEntity<String> responseObj = restTemplate.exchange(url, HttpMethod.GET,entityReq, String.class, params);
        return responseObj.getBody();
    }

}
