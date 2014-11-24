package com.oa.cgpg.dataOperations;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.oa.cgpg.MainActivity;
import com.oa.cgpg.customControls.NoConnectionDialog;
import com.oa.cgpg.models.opinionNetEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tomasz on 2014-11-07.
 */
public class XMLOpinionGet extends AsyncTask<Void, Void, Void> {
    private static final String HTTP_URL = "http://cgpg.zz.mu/webservice1.php?";
    private static final String ns = null;
    private String xml;
    private ProgressDialog progressDialog;
    private Context context;
    private int userId;
    private int poiId;
    public AsyncResponse delegate = null;

    public XMLOpinionGet(Context c, int userid, int poiid) {
        this.context = c;
        this.userId = userid;
        this.poiId = poiid;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = ProgressDialog.show(context, "Pobierane danych", "Proszę czekać...", true, false);
    }

    @Override
    protected void onPostExecute(Void D) {
        progressDialog.dismiss();
        if (xml != null)
            delegate.processFinishOpinion(getList());
        else {
            NoConnectionDialog dialog = new NoConnectionDialog();
            dialog.show(((MainActivity) context).getFragmentManager(), "noConnection");
        }


    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // defaultHttpClient
            Log.i(getClass().getName(), "Create client");
            DefaultHttpClient httpClient = new DefaultHttpClient();
            Log.i(getClass().getName(), "Create request");
            HttpPost httpPost = new HttpPost(HTTP_URL + "user=" + userId + "&poiid=" + poiId);
            Log.i(getClass().getName(), "Execute request");
            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.i(getClass().getName(), "Get response");
            HttpEntity httpEntity = httpResponse.getEntity();
            Log.i(getClass().getName(), "Parse xml");
            xml = EntityUtils.toString(httpEntity);
            Log.i(getClass().getName(), "XML Length " + xml.length());
            //  Log.i(getClass().getName(), xml);
        } catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), "UnsupportedEncodingException");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e(getClass().getName(), "ClientProtocolException");
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

    public List getList() {
        List opinions = null;
        try {
            InputStream is = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            opinions = parse(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return opinions;
    }

    public List parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readOpinions(parser);
        } finally {
            in.close();
        }
    }

    private List readOpinions(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        List opinions = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "opinions");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("opinion")) {
                opinions.add(readOpinion(parser));
            } else {
                skip(parser);
            }
        }
        return opinions;
    }

    private opinionNetEntity readOpinion(XmlPullParser parser) throws XmlPullParserException, IOException,
            ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "opinion");

        int id = 0;
        String opinionText = "";
        String username = "";
        int poiId = 0;
        int ratingPlus = 0;
        int ratingMinus = 0;
        int val = 0;
        int opinionType = 0;
        Date addDate = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
                id = readId(parser);
            } else if (name.equals("opinionText")) {
                opinionText = readOpinionText(parser);
            } else if (name.equals("username")) {
                username = readUsername(parser);
            } else if (name.equals("poiID")) {
                poiId = readPoiId(parser);
            } else if (name.equals("ratingPlus")) {
                ratingPlus = readRatingPlus(parser);
            } else if (name.equals("ratingMinus")) {
                ratingMinus = readRatingMinus(parser);
            } else if (name.equals("setVal")) {
                val = readSetVal(parser);
            } else if (name.equals("opinionType")) {
                opinionType = readOpinionType(parser);
            } else if (name.equals("addDate")) {
                addDate = readAddDate(parser);
            } else {
                skip(parser);
            }
        }
        return new opinionNetEntity(id, opinionText, username, poiId, ratingPlus, ratingMinus, val, opinionType,
                addDate);
    }

    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "id");
        int id = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "id");
        return id;
    }

    private String readOpinionText(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "opinionText");
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "opinionText");
        return text;
    }

    private String readUsername(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "username");
        String username = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "username");
        return username;
    }

    private int readPoiId(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "poiID");
        int poiId = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "poiID");
        return poiId;
    }

    private int readRatingPlus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ratingPlus");
        int ratePlus = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "ratingPlus");
        return ratePlus;
    }

    private int readRatingMinus(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ratingMinus");
        int rateMinus = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "ratingMinus");
        return rateMinus;
    }

    private Date readAddDate(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        Date data;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        parser.require(XmlPullParser.START_TAG, ns, "addDate");
        String date = readText(parser);
        data = format.parse(date);
        parser.require(XmlPullParser.END_TAG, ns, "addDate");
        return data;
    }

    private int readSetVal(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "setVal");
        String tmp = readText(parser);
        int val = -1;
        Log.i(getClass().getName(), "TMP VALUE = " + tmp);
        if (tmp.equals("")) {
            val = -1;
        } else if (tmp.equals("0")) {
            val = 0;
        } else if (tmp.equals("1")) {
            val = 1;
        }

        parser.require(XmlPullParser.END_TAG, ns, "setVal");
        return val;
    }

    private int readOpinionType(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "opinionType");
        int opType = Integer.parseInt(readText(parser));
        parser.require(XmlPullParser.END_TAG, ns, "opinionType");
        return opType;
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
