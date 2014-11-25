package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.oa.cgpg.LoggedUserInfo;
import com.oa.cgpg.models.opinionRateNet;
import com.oa.cgpg.models.opinionRatingUpdateNet;
import com.oa.cgpg.models.poiEntity;
import com.oa.cgpg.models.typeEntity;

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
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-24.
 */
public class XMLOpinionRateUpdate extends AsyncTask<Void, Void, Void> {
    private ProgressDialog progressDialog;
    private Context context;
    List<Integer> listOfId;
    private String xml;
    List<opinionRatingUpdateNet> listOfUpdate;
    private static final String ns = null;
    private dbOps dbOps;
    public AsyncResponse delegate = null;

    public XMLOpinionRateUpdate(dbOps dbOps, List<Integer> list, Context context) {
        this.listOfId = list;
        this.context = context;
        this.dbOps = dbOps;
        listOfUpdate = new ArrayList<opinionRatingUpdateNet>();
    }

    @Override
    protected void onPreExecute() {
      //  progressDialog = ProgressDialog.show(context, "Update poi ratings", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void D) {
        delegate.processFinish("");
       // progressDialog.dismiss();

    }

    protected Void doInBackground(Void... params) {
        try {
            listToXmlString();
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
            xml = EntityUtils.toString(httpEntity);
            update();
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
        xmlSerializer.startTag("", "PoiRates");
        for (Integer id : listOfId) {
            xmlSerializer.startTag("", "Poi");

            xmlSerializer.startTag("", "id");
            xmlSerializer.text(id + "");
            xmlSerializer.endTag("", "id");

            xmlSerializer.endTag("", "Poi");
        }
        xmlSerializer.endTag("", "PoiRates");
        xmlSerializer.endDocument();

        xml = writer.toString();
    }

    private void update() {
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            getList(is);
            for (opinionRatingUpdateNet oRUN : listOfUpdate) {
                poiEntity poi = dbOps.getPoiById(oRUN.getId());
                poi.setRatingPlus(oRUN.getRatingPlus());
                poi.setRatingMinus(oRUN.getRatingMinus());
                dbOps.commitPOI(poi);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getList(InputStream is) {
        try {
            parse(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readXML(parser);
        } finally {
            in.close();
        }
    }

    private void readXML(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "poiRates");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("poiRate")) {
                readRates(parser);
            } else {
                skip(parser);
            }
        }
    }

    private void readRates(XmlPullParser parser) throws XmlPullParserException, IOException,
            ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "poiRate");

        int id = 0;
        int ratePlus = 0;
        int rateMinus = 0;


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String nodeName = parser.getName();
            if (nodeName.equals("id")) {
                id = readInt(parser, "id");
            } else if (nodeName.equals("ratePlus")) {
                ratePlus = readInt(parser, "ratePlus");
            } else if (nodeName.equals("rateMinus")) {
                rateMinus = readInt(parser, "rateMinus");
            } else {
                skip(parser);
            }
        }
        listOfUpdate.add(new opinionRatingUpdateNet(id, ratePlus, rateMinus));

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
