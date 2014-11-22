package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import com.oa.cgpg.LoggedUserInfo;
import com.oa.cgpg.models.opinionNetEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-18.
 */
public class XMLOpinionSend extends AsyncTask<Void,Void,Void> {
    private ProgressDialog progressDialog;
    private Context context;
    private String xml;
    private List<opinionNetEntity> list;
    public AsyncResponse delegate = null;



    public XMLOpinionSend(Context context, List<opinionNetEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    protected void onPreExecute () {
        progressDialog = ProgressDialog.show(context, "Dodawanie opinii", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute (Void D) {
        delegate.processFinish("");
        progressDialog.dismiss();

    }

    @Override
    protected Void doInBackground (Void... voids) {
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
        LUI.setUserId(1);
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.startTag("","Opinions");
        for(opinionNetEntity op : list){
            xmlSerializer.startTag("","Opinion");

            xmlSerializer.startTag("","opinionText");
            xmlSerializer.text(op.getOpinionText());
            xmlSerializer.endTag("","opinionText");

            xmlSerializer.startTag("","user");
            xmlSerializer.text(LUI.getUserId()+"");
            xmlSerializer.endTag("","user");

            xmlSerializer.startTag("","poiId");
            xmlSerializer.text(op.getPoiId()+"");
            xmlSerializer.endTag("","poiId");

            xmlSerializer.startTag("","opinionType");
            xmlSerializer.text(op.getOpinionType()+"");
            xmlSerializer.endTag("","opinionType");
/*
            xmlSerializer.startTag("","date");
            xmlSerializer.text(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(op.getAddDate()));
            xmlSerializer.endTag("","date");
*/
            xmlSerializer.endTag("","Opinion");
        }
        xmlSerializer.endTag("","Opinions");
        xmlSerializer.endDocument();

        xml = writer.toString();
    }

    public String getXML(){
        return this.xml;
    }
}
