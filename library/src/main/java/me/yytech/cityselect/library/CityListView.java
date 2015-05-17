package me.yytech.cityselect.library;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Young on 2015/5/17.
 */
public class CityListView extends FrameLayout implements AdapterView.OnItemClickListener, StickyListHeadersListView.OnStickyHeaderChangedListener {

    private StickyListHeadersListView mStickyListHeadersListView;

    public interface OnCitySelect {
        void onSelect(String city);
    }

    private OnCitySelect mOnCitySelect;

    public void setOnCitySelect(OnCitySelect pOnCitySelect) {
        mOnCitySelect = pOnCitySelect;
    }

    public CityListView(Context context) {
        super(context);
        initData();
    }

    public CityListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public CityListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData();
    }

    private void initData() {
        LayoutInflater.from(getContext()).inflate(R.layout.cityselect_item_layout_view, this);
        mStickyListHeadersListView = (StickyListHeadersListView) findViewById(R.id.lvCity);
        mStickyListHeadersListView.setOnStickyHeaderChangedListener(this);
        mStickyListHeadersListView.setEmptyView(findViewById(R.id.empty_view));
        new AsyncTask<Void, Void, Void>() {

            ArrayList<String> strings = new ArrayList<String>();
            ArrayList<Character> letters = new ArrayList<Character>();

            @Override
            protected Void doInBackground(Void... params) {
                XmlResourceParser xrp = getResources().getXml(R.xml.cities);
                try {
                    while (xrp.next() != XmlResourceParser.START_TAG) {
                        continue;
                    }
                    xrp.next();
                    int readCount = 0;
                    while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                        while (xrp.getEventType() != XmlResourceParser.START_TAG) {
                            if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
                                return null;
                            }
                            xrp.next();
                        }
                        if (xrp.getName().equals("City")) {
                            String s = xrp.nextText();
                            strings.add(s);
                            if (s.length() > 0) {
                                letters.add(PinyinHelper.toHanyuPinyinStringArray(s.charAt(0))[0].charAt(0));
                            }
                        }
                        while (xrp.getEventType() != XmlResourceParser.END_TAG) {
                            xrp.next();
                        }
                        xrp.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Activity activity = (Activity) getContext();
                if (!activity.isFinishing()) {
                    String[] stringList = strings.toArray(new String[strings.size()]);
                    Arrays.sort(stringList, new CityComparator());
                    Character[] letterArray = letters.toArray(new Character[letters.size()]);
                    Arrays.sort(letterArray, new CharacterComparator());
                    CityAdapter adapter = new CityAdapter(stringList, letterArray);
                    mStickyListHeadersListView.setAdapter(adapter);
                    QuickScroll quickScroll = (QuickScroll) findViewById(R.id.quickscroll);
                    quickScroll.init(QuickScroll.TYPE_POPUP, mStickyListHeadersListView, adapter, QuickScroll.STYLE_HOLO);
                    mStickyListHeadersListView.setOnItemClickListener(CityListView.this);
                }
            }
        }.execute();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
        header.setAlpha(1);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String city = (String) parent.getAdapter().getItem(position);
        if (mOnCitySelect != null) {
            mOnCitySelect.onSelect(city);
        }
    }

    private class CityAdapter extends BaseAdapter implements StickyListHeadersAdapter, Scrollable {

        private String[] mStringList;
        private Character[] mStringLetters;

        public CityAdapter(String[] pStringList, Character[] pStringLetters) {
            mStringList = pStringList;
            mStringLetters = pStringLetters;
        }

        @Override
        public int getCount() {
            if (mStringList == null) {
                return 0;
            }
            return mStringList.length;
        }

        @Override
        public Object getItem(int position) {
            if (mStringList == null) {
                return null;
            }
            return mStringList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.cityselect_item_city, null);
            }
            TextView textView = (TextView) convertView;
            textView.setText((CharSequence) getItem(position));
            return convertView;
        }

        @Override
        public View getHeaderView(int i, View pView, ViewGroup pViewGroup) {
            if (pView == null) {
                pView = LayoutInflater.from(getContext()).inflate(R.layout.cityselect_item_city_header, null);
            }
            TextView textView = (TextView) ((ViewGroup) pView).getChildAt(0);
            textView.setText(mStringLetters[i].toString().toUpperCase());
            return pView;
        }

        @Override
        public long getHeaderId(int i) {
            return (mStringLetters[i]).charValue();
        }

        @Override
        public String getIndicatorForPosition(int childposition, int groupposition) {
            return String.valueOf(mStringLetters[childposition].charValue());
        }

        @Override
        public int getScrollPosition(int childposition, int groupposition) {
            return childposition;
        }
    }
}
