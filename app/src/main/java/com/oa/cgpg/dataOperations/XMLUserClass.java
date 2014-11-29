package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.oa.cgpg.models.opinionRatingUpdateNet;
import com.oa.cgpg.models.userNetEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.ParseException;

/**
 * Created by Tomasz on 2014-11-21.
 */
public class XMLUserClass extends AsyncTask<Void, Void, Void> {
    private ProgressDialog progressDialog;
    private Context context;
    private AsyncResponse delegate = null;
    private String user;
    private int userId;
    private String pass;
    private String newPass;
    private String email;
    private String xml;
    private String response;
    private boolean register;
    private boolean update;
    private userNetEntity userNetEntity;
    private static final String ns = null;

    public XMLUserClass(Context context, AsyncResponse d, String user, String pass, String email) {
        this.context = context;
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.delegate = d;
        this.register = true;
        this.update = false;
    }

    public XMLUserClass(Context context, AsyncResponse d, String user, String pass) {
        this.context = context;
        this.user = user;
        this.pass = pass;
        this.delegate = d;
        this.register = false;
        this.update = false;
    }

    public XMLUserClass(Context context, AsyncResponse delegate, int user, String pass, String newPass, String email) {
        this.context = context;
        this.delegate = delegate;
        this.userId = user;
        this.pass = pass;
        this.newPass = newPass;
        this.email = email;
        this.register = false;
        this.update = true;
    }

    @Override
    protected void onPreExecute() {
        if (register == true && update == false)
            progressDialog = ProgressDialog.show(context, "Trwa rejestracja", "Proszę czekać...", true, false);
        else if (register == false && update == false)
            progressDialog = ProgressDialog.show(context, "Trwa logowanie", "Proszę czekać...", true, false);
        else if (update == true)
            progressDialog = ProgressDialog.show(context, "Trwa zmiana danych", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void D) {
        if (register == false && update == false) {
            delegate.processFinish(userNetEntity);
        } else
            delegate.processFinish(response);
        progressDialog.dismiss();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if (register == true && update == false)
                userToXmlRegister();
            else if (register == false && update == false)
                userToXmlLogin();
            else if (update == true)
                userToXmlUpdate();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://cgpg.zz.mu/webservice2.php");
            httpPost.addHeader("Content-Type", "application/xml");
            Log.i(getClass().getName(), "create connection");
            StringEntity entity = null;
            entity = new StringEntity(xml, "UTF-8");
            Log.i(getClass().getName(), "xml length " + xml.length());
            entity.setContentType("application/xml");
            Log.i(getClass().getName(), "setEntityContent");
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            if (register == false && update == false) {
                parse();
            }
            Log.i("SEND ENTITY", httpPost.getMethod());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error");
            e.printStackTrace();
        }
        return null;
    }

    private void userToXmlRegister() throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("", "UsersReg");
        xmlSerializer.startTag("", "User");

        xmlSerializer.startTag("", "username");
        xmlSerializer.text(this.user);
        xmlSerializer.endTag("", "username");

        xmlSerializer.startTag("", "password");
        xmlSerializer.text(this.pass);
        xmlSerializer.endTag("", "password");

        xmlSerializer.startTag("", "email");
        xmlSerializer.text(this.email);
        xmlSerializer.endTag("", "email");

        xmlSerializer.endTag("", "User");
        xmlSerializer.endTag("", "UsersReg");
        xmlSerializer.endDocument();

        xml = writer.toString();
    }

    private void userToXmlLogin() throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("", "UsersLog");
        xmlSerializer.startTag("", "User");

        xmlSerializer.startTag("", "username");
        xmlSerializer.text(this.user);
        xmlSerializer.endTag("", "username");

        xmlSerializer.startTag("", "password");
        xmlSerializer.text(this.pass);
        xmlSerializer.endTag("", "password");

        xmlSerializer.endTag("", "User");
        xmlSerializer.endTag("", "UsersLog");
        xmlSerializer.endDocument();

        xml = writer.toString();
    }

    private void userToXmlUpdate() throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("", "UsersUpdate");
        xmlSerializer.startTag("", "User");

        xmlSerializer.startTag("", "userId");
        xmlSerializer.text(this.userId + "");
        xmlSerializer.endTag("", "userId");

        xmlSerializer.startTag("", "password");
        xmlSerializer.text(this.pass);
        xmlSerializer.endTag("", "password");

        xmlSerializer.startTag("", "email");
        xmlSerializer.text(this.email);
        xmlSerializer.endTag("", "email");

        if (!newPass.isEmpty()) {
            xmlSerializer.startTag("", "newPass");
            xmlSerializer.text(this.newPass);
            xmlSerializer.endTag("", "newPass");
        }

        xmlSerializer.endTag("", "User");
        xmlSerializer.endTag("", "UsersUpdate");
        xmlSerializer.endDocument();

        xml = writer.toString();
    }

    public String getXML() {
        return this.xml;
    }

    public void parse() throws XmlPullParserException, IOException, ParseException {
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(response.getBytes("UTF-8"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readUserData(parser);
        } finally {
            in.close();
        }
    }

    private void readUserData(XmlPullParser parser) throws XmlPullParserException, IOException,
            ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "User");

        int id = 0;
        String username = null;
        String email = null;


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nodeName = parser.getName();
            if (nodeName.equals("id")) {
                id = readInt(parser, "id");
            } else if (nodeName.equals("username")) {
                username = readString(parser, "username");
            } else if (nodeName.equals("email")) {
                email = readString(parser, "email");
            } else {
                skip(parser);
            }
        }
        userNetEntity = new userNetEntity(id, username, email);

    }

    private int readInt(XmlPullParser parser, String nodeName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, nodeName);
        int tmp = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, nodeName);
        return tmp;
    }

    private String readString(XmlPullParser parser, String nodeName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, nodeName);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, nodeName);
        return text;
    }


    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}