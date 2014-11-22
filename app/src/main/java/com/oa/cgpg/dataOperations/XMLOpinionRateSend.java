package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.oa.cgpg.LoggedUserInfo;
import com.oa.cgpg.models.opinionRateNet;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-21.
 */
public class XMLOpinionRateSend extends AsyncTask<Void, Void, Void> {
    private List<opinionRateNet> list;
    private String xml;
    private ProgressDialog progressDialog;
    private Context context;
    public AsyncResponse delegate = null;

    public XMLOpinionRateSend(List<opinionRateNet> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Dodawanie oceny", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void D) {
        delegate.processFinish("");
        progressDialog.dismiss();

    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            listToXmlString();
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
            httpclient.execute(httpPost);

            Log.i("SEND ENTITY", httpPost.getMethod());
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error");
            e.printStackTrace();
        }
        return null;
    }

    private void listToXmlString() throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        LoggedUserInfo LUI = LoggedUserInfo.getInstance();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("", "OpinionsRate");
        for (opinionRateNet opRate : list) {
            xmlSerializer.startTag("", "OpinionRate");

            xmlSerializer.startTag("", "userId");
            xmlSerializer.text(opRate.getUserId() + "");
            xmlSerializer.endTag("", "userId");

            xmlSerializer.startTag("", "opinionId");
            xmlSerializer.text(opRate.getOpinionId() + "");
            xmlSerializer.endTag("", "opinionId");

            xmlSerializer.startTag("", "val");
            xmlSerializer.text(opRate.getValue() + "");
            xmlSerializer.endTag("", "val");

            xmlSerializer.endTag("", "OpinionRate");
        }
        xmlSerializer.endTag("", "OpinionsRate");
        xmlSerializer.endDocument();

        xml = writer.toString();
    }

    public String getXML() {
        return this.xml;
    }
}
