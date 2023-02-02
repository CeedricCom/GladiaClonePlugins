package com.ceedric.eventkoth.view.paste;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PasteBinPasteTransmitter implements PasteTransmitter {

    @Override
    public String send(InputStream stream) throws IOException {
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://pastebin.com/api/api_post.php");

        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("api_dev_key", "HaxhzR2-3nkUq-Nnaz9B_SKb3_9TidZm"));
        params.add(new BasicNameValuePair("api_option", "paste"));
        params.add(new BasicNameValuePair(" api_paste_code",fromInputStream(stream)));
        params.add(new BasicNameValuePair("api_paste_private","0"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            try (InputStream instream = entity.getContent()) {
                return fromInputStream(instream);
            }
        }

        return null;
    }

    public String fromInputStream(InputStream inputStream) {
        String text = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        return text;
    }
}
