package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.oa.cgpg.LoggedUserInfo;
import com.oa.cgpg.models.opinionNetEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;

/**
 * Created by Tomasz on 2014-11-21.
 */
public class XMLUserClass extends AsyncTask<Void, Void, Void> {
    private ProgressDialog progressDialog;
    private Context context;
    public AsyncResponse delegate = null;
    private String user;
    private String pass;
    private String email;
    private String xml;
    private String response;
    private boolean register;

    public XMLUserClass(Context context, AsyncResponse d, String user, String pass, String email) {
        this.context = context;
        this.user = user;
        this.pass = pass;
        this.email = email;
        this.delegate = d;
        this.register = true;
    }

    public XMLUserClass(Context context, AsyncResponse d, String user, String pass) {
        this.context = context;
        this.user = user;
        this.pass = pass;
        this.delegate = d;
        this.register = false;
    }


    @Override
    protected void onPreExecute() {
        if(register)
            progressDialog = ProgressDialog.show(context, "Rejestracja użytkownika", "Proszę czekać...", true, false);
        else
            progressDialog = ProgressDialog.show(context, "Logowanie użytkownika", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void D) {
        delegate.processFinish(response);
        progressDialog.dismiss();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            if(register)
                userToXmlRegister();
            else
                userToXmlLogin();
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://cgpg.zz.mu/webservice2.php");
            httpPost.addHeader("Content-Type", "application/xml");
            Log.i(getClass().getName(), "create connection");
            StringEntity entity = null;
            entity = new StringEntity(xml, "UTF-8");
            Log.i(getClass().getName(), "xml length "+xml.length());
            entity.setContentType("application/xml");
            Log.i(getClass().getName(), "setEntityContent");
            httpPost.setEntity(entity);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
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
    public String getXML(){
        return this.xml;
    }
}
