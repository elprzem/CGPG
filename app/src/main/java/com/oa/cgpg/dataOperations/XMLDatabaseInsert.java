package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Tomasz on 2014-11-07.
 */
public class XMLDatabaseInsert extends AsyncTask<Void, Void, Void> {
    private static final String HTTP_URL = "http://cgpg.zz.mu/output.xml";
    private String xml;
    private ProgressDialog progressDialog;
    private Context context;
    public AsyncResponse delegate=null;

    public XMLDatabaseInsert(Context c, String s) {
        this.context = c;
        this.xml = s;
    }
    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Pobierane danych", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void  D) {
        progressDialog.dismiss();
        delegate.processFinish(xml);
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // defaultHttpClient
            Log.i(getClass().getName(),"Create client");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.i(getClass().getName(),"Create request");
            HttpPost httpPost = new HttpPost(HTTP_URL);
            Log.i(getClass().getName(),"Execute request");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.i(getClass().getName(),"Get response");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.i(getClass().getName(),"Parse xml");
            xml = EntityUtils.toString(httpEntity);
            Log.i(getClass().getName(),xml.length()+"");
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(),"UnsupportedEncodingException");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e(getClass().getName(),"ClientProtocolException");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(getClass().getName(), "IOException");
            e.printStackTrace();
        }
        return null;
    }

    public String getXmlFromUrl() {
        return xml;
    }

    public Document getDomElement(){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            Log.i(getClass().getName(),"dlugosc xml"+xml.length());
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }
    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }
}
