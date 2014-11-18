package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;
import com.oa.cgpg.LoggedUserInfo;
import com.oa.cgpg.models.opinionNetEntity;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-18.
 */
public class XMLOpinionSendParsing extends AsyncTask<Void,Void,Void> {
    private ProgressDialog progressDialog;
    private Context context;
    private String xml;
    private List<opinionNetEntity> list;
    public AsyncResponse delegate = null;



    public XMLOpinionSendParsing (Context context,List<opinionNetEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    protected void onPreExecute () {
        progressDialog = ProgressDialog.show(context, "Pobierane danych", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute (Void D) {
        progressDialog.dismiss();

    }

    @Override
    protected Void doInBackground (Void... voids) {
        return null;
    }

    public void listToXmlString() throws Exception {
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        LoggedUserInfo LUI = LoggedUserInfo.getInstance();
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

            xmlSerializer.startTag("","date");
            xmlSerializer.text(op.getAddDate()+"");
            xmlSerializer.endTag("","date");

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
