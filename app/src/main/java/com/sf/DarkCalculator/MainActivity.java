package com.sf.DarkCalculator;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.sf.ExpressionHandler.Constants;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * █████▒█    ██  ▄████▄   ██ ▄█▀         ██████╗ ██╗   ██╗ ██████╗
 * ▓██   ▒ ██  ▓██▒▒██▀ ▀█   ██▄█▒        ██╔══██╗██║   ██║██╔════╝
 * ▒████ ░▓██  ▒██░▒▓█    ▄ ▓███▄░        ██████╔╝██║   ██║██║  ███╗
 * ░▓█▒  ░▓▓█  ░██░▒▓▓▄ ▄██▒▓██ █▄        ██╔══██╗██║   ██║██║   ██║
 * ░▒█░   ▒▒█████▓ ▒ ▓███▀ ░▒██▒ █▄       ██████╔╝╚██████╔╝╚██████╔╝
 * ▒ ░   ░▒▓▒ ▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒        ╚═════╝  ╚═════╝  ╚═════╝
 * ░     ░░▒░ ░ ░   ░  ▒   ░ ░▒ ▒░
 * ░ ░    ░░░ ░ ░ ░        ░ ░░ ░
 * ░     ░ ░      ░  ░
 */

public class MainActivity extends BaseActivity {

    public static MainActivity activity;
    public static int screenOrient;
    private Context context;
    private Toolbar toolbar;
    private EditText editText;
    private TextView stateText;
    private TextView out;
    private ViewPager drawerPager;
    private DrawerLayout drawer;
    private ArrayList<View> drawerPageList;

    private Pattern keywords;
    final private String[] operator = {"DEL", "÷", "×", "-", "+", "%", ",", "i"};
    final private String[] operatorVice = {"CLR", "√", "^", "!", "()", "°", "∞", "x"};

    final private String[][] function = {
            {"sqrt", "cbrt", "root", "rand", "randInt", "lg", "ln", "log",
                    "abs", "min", "max", "fact", "sin", "cos", "tan", "asin", "acos",
                    "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", "recipr",
                    "sum", "re", "im", "arg", "norm", "reg", "conj", "diff", "limit",
                    "eval", "fzero", "integ", "exp", "gcd", "lcm", "perm", "comb", "round",
                    "floor", "ceil", "sign", "gamma", "remn", "reduc", "prime", "isPrime",
                    "isOdd", "toDEG", "toRAD", "reStart", "setPrec", "setBase", "setCR", "setTS"},
            {"ans", "reg", "π", "e", "F", "h", "ћ", "γ", "φ", "c", "N", "R", "k", "G", "Φ", "me", "mn", "mp"}};

    final private String[][] functionVice = {
            {"平方根", "立方根", "开方", "随机复数", "随机整数", "常用对数", "自然对数", "对数",
                    "绝对值", "最小", "最大", "阶乘", "正弦", "余弦", "正切", "反正弦", "反余弦",
                    "反正切", "双曲正弦", "双曲余弦", "双曲正切", "反双曲正弦", "反双曲余弦",
                    "反双曲正切", "倒数", "累加求和", "实部", "虚部", "辐角", "模长", "寄存",
                    "共轭复数", "导函数", "极限", "求值", "函数零点", "定积分", "e底指数",
                    "最大公约", "最小公倍", "排列", "组合", "四舍五入", "向下取整", "向上取整",
                    "取正负号", "伽玛函数", "取余", "分数化简", "质数", "判断质数", "判断奇数",
                    "转角度", "转弧度", "重启APP", "输出精度", "输出进制", "排列方式", "字体大小"}, {
            "上次运算", "寄存器", "圆周率", "自然底数", "法拉第", "普朗克", "约化普朗克", "欧拉", "黄金分割",
            "光速", "阿伏伽德罗", "理想气体", "玻尔兹曼", "万有引力", "磁通量子", "电子质量", "质子质量", "中子质量"}};

    final private String[] functionList = {"科学计算", "大数计算", "时间计算", "进制转换",
            "方程式配平", "分子量计算", "亲戚关系计算", "大写数字", "汇率转换", "单位转换"};

    private String[] numeric = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "·", "0", "=", "A", "B", "C", "D", "E", "F",
            "⑵", "⑶", "⑷", "⑸", "⑹", "⑺", "⑻", "⑼", "⑽", "⑾", "⑿", "⒀", "⒁", "⒂", "⒃"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        context = this;
        screenOrient = getScreenOrient(this);
        barAdapter.removeAll(barAdapter);
        barView.removeAll(barView);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initKeyWords();
        initToolBar();
        initEditText();
        initTextView();
        initDrawer();
        initPages();
        initTabs();
        initSideBar();
        initNumeric();
        initOperator();
        initOperatorPro();
        initCR();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void initCR() {
        if (screenOrient == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            int[] y = {1, 3, 1, 3, 3};
            for (int i = 0; i < y.length; i++)
                barView.get(i).setNumColumns(preferences.getInt("CRy" + screenOrient + ("" + i), y[i]));

            int[] z = {6, 4, 5, 5, 5};
            for (int i = 0; i < z.length; i++)
                barAdapter.get(i).setValue(preferences.getInt("CRz" + screenOrient + ("" + i), z[i]));
        } else {
            stateText.setTextSize(16);
            out.setTextSize(24);

            int[] y = {2, 4, 2, 5, 5};
            for (int i = 0; i < y.length; i++)
                barView.get(i).setNumColumns(preferences.getInt("CRy" + screenOrient + ("" + i), y[i]));

            int[] z = {3, 3, 3, 3, 3};
            for (int i = 0; i < z.length; i++)
                barAdapter.get(i).setValue(preferences.getInt("CRz" + screenOrient + ("" + i), z[i]));
        }
    }

    public int getScreenOrient(Activity activity) {
        int orient = activity.getRequestedOrientation();
        if (orient != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE && orient != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            WindowManager windowManager = activity.getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            orient = screenWidth < screenHeight ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
        return orient;
    }

    private void initKeyWords() {
        StringBuffer sb = new StringBuffer();
        sb.append("\\b(");
        for (String[] array : function)
            for (String str : array) {
                sb.append(str + "|");
            }
        sb.append(")\\b");
        keywords = Pattern.compile(sb.toString());
    }

    private void initTextView() {
        stateText = (TextView) findViewById(R.id.text_state);
        stateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateText.setText(null);
            }
        });
        out = (TextView) findViewById(R.id.text_out);
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (out.getText().toString().indexOf("重启") != -1) {
                    editText.setText("reStart()");
                    return;
                }
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(rootValue);
                Snackbar.make(v, "已复制运算结果", Snackbar.LENGTH_SHORT).show();
            }
        });
        out.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ResultsActivity.actionStart(v.getContext(), rootValue);
                return true;
            }
        });
    }

    private void initSideBar() {
        final GridView sideBar = (GridView) findViewById(R.id.sideBar);
        barView.add(sideBar);
        sideBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view.findViewById(R.id.text_item);
                String text = textView.getText().toString();
                int i = 0;
                for (String str : functionList) {
                    i++;
                    if (text.equals(str))
                        break;
                }
                switch (i) {
                    case 1:
                        break;
                    case 2:
                        BigDecimalActivity.actionStart(context);
                        break;
                    case 4:
                        BaseConversionActivity.actionStart(context);
                        break;
                    case 8:
                        CapitalMoneyActivity.actionStart(context);
                        break;
                    default:
                        Snackbar.make(sideBar, "功能还未完善", Snackbar.LENGTH_SHORT).show();
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        GridViewAdapter sideBarAdapter = new GridViewAdapter(this, sideBar, Arrays.asList(functionList), R.layout.button_sidebar);
        barAdapter.add(sideBarAdapter);
        sideBar.setAdapter(sideBarAdapter);
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main);
        drawer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    drawer.closeDrawers();
                }
                return false;
            }
        });
    }

    private void initTabs() {
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs_main);
        tabs.setupWithViewPager(drawerPager);
        tabs.getTabAt(0).setText("函数");
        tabs.getTabAt(1).setText("常数");
    }

    private void initPages() {
        drawerPageList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            GridView gridView = new GridView(this);
            gridView.setFastScrollEnabled(true);
            drawerPageList.add(gridView);
        }

        drawerPager = (ViewPager) findViewById(R.id.viewPager_drawer);
        MainPagerAdapter drawerPagerAdapter = new MainPagerAdapter(drawerPageList);
        drawerPager.setAdapter(drawerPagerAdapter);
        drawerPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    drawer.openDrawer(GravityCompat.END);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static ArrayList<GridView> barView = new ArrayList<>();
    public static ArrayList<GridViewAdapter> barAdapter = new ArrayList<>();

    public void setBarCR(final int x, final int y, final int z) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("CRy" + screenOrient + ("" + x), y);
        editor.putInt("CRz" + screenOrient + ("" + x), z);
        editor.apply();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barView.get(x).setNumColumns(y);
                barAdapter.get(x).setValue(z);
            }
        });
    }


    private void initOperatorPro() {
        int i = 0;
        for (View view : drawerPageList) {
            GridView operatorProBar = (GridView) view;
            barView.add(operatorProBar);
            final String s = i == 0 ? "()" : "";
            operatorProBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String str = ((TextView) view.findViewById(R.id.text_item)).getText().toString();
                    if (str.equals("reduc")) {
                        Snackbar.make(view, "此函数还未完善", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Editable editable = editText.getText();
                    int index = editText.getSelectionStart();
                    editable.insert(index, str + s);
                    if (s.length() != 0)
                        editText.setSelection(index + str.length() + s.length() - 1);
                }
            });
            int id = i == 0 ? R.layout.button_function : R.layout.button_constant;
            GridViewAdapter operatorProAdapter = new GridViewAdapter(this, operatorProBar, Arrays.asList(function[i++]), id);
            operatorProAdapter.setViceText(Arrays.asList(functionVice[i - 1]));

            barAdapter.add(operatorProAdapter);
            operatorProBar.setAdapter(operatorProAdapter);
        }
    }

    private void initOperator() {
        GridView operatorBar = (GridView) findViewById(R.id.bar_operator);
        barView.add(operatorBar);
        operatorBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = ((TextView) view.findViewById(R.id.text_item)).getText().toString();
                Editable editable = editText.getText();
                int index = editText.getSelectionStart();
                if (str.equals("DEL")) {
                    if (index == 0) {
                        return;
                    }
                    editable.delete(index - 1, index);
                    return;
                }
                editable.insert(index, str);
            }
        });
        operatorBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String str = ((TextView) view.findViewById(R.id.text_vice_item)).getText().toString();
                if (str.equals("CLR")) {
                    editText.setText(null);
                    return true;
                }
                int index = editText.getSelectionStart();
                editText.getText().insert(index, str);
                if (str.equals("()"))
                    editText.setSelection(index + str.length() - 1);
                return true;
            }
        });
        GridViewAdapter operatorAdapter = new GridViewAdapter(this, operatorBar, Arrays.asList(operator), R.layout.button_operator);
        barAdapter.add(operatorAdapter);
        operatorAdapter.setViceText(Arrays.asList(operatorVice));
        operatorBar.setAdapter(operatorAdapter);
    }

    private void initNumeric() {
        if (screenOrient == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            numeric = new String[]{"7", "8", "9", "A", "4", "5", "6", "B", "1", "2", "3", "C", "·", "0", "=", "D",
                    "⑵", "⑶", "⑷", "E", "⑸", "⑹", "⑺", "F", "⑻", "⑼", "⑽", "⑾", "⑿", "⒀", "⒁", "⒂", "⒃"};
        GridView numericBar = (GridView) findViewById(R.id.bar_numeric);
        barView.add(numericBar);
        numericBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = ((TextView) view.findViewById(R.id.text_item)).getText().toString();
                int index = editText.getSelectionStart();
                if (str.equals("=")) {
                    if (calcThread != null) {
                        Snackbar.make(view, "请等待运算完成", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    stateText.setText("运算中...");
                    calcThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final long t = System.currentTimeMillis();
                            final String[] value = ExpressionHandler.calculation(editText.getText().toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    stateText.setText("运算结束，耗时 " + (System.currentTimeMillis() - t) + " 毫秒");
                                    if (value[1].equals("true")) {
                                        out.setTextColor(0xffff4081);
                                        out.setText(value[0]);
                                    } else {
                                        out.setTextColor(0xffeeeeee);
                                        Constants.constants.set(0, new String[]{"ans", value[0]});
                                        if (value[0].getBytes().length > 1000) {
                                            out.setText("数值太大，请长按此处显示结果");
                                            ResultsActivity.actionStart(context, value[0]);
                                        } else
                                            out.setText(value[0]);
                                    }
                                    calcThread = null;
                                    rootValue = value[0];
                                }
                            });
                        }
                    });
                    calcThread.start();
                    return;
                }

                str = str.equals("·") ? "." : str;
                editText.getText().insert(index, str);
            }
        });
        GridViewAdapter numericAdapter = new GridViewAdapter(this, numericBar, Arrays.asList(numeric), R.layout.button_numeric);
        barAdapter.add(numericAdapter);
        numericBar.setAdapter(numericAdapter);
    }

    private boolean modified = true;
    private int selection = 0;
    private Thread calcThread;
    private String rootValue;

    private void initEditText() {
        editText = (EditText) findViewById(R.id.editText);

        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText, false);
        } catch (Exception e) {
        }
        AutofitHelper.create(editText).setMinTextSize(28);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.requestFocusFromTouch();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    stateText.setText(null);
                    out.setText("···");
                    return;
                }
                if (calcThread == null) {
                    stateText.setText("运算中...");
                    calcThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final long t = System.currentTimeMillis();
                            final String[] value = ExpressionHandler.calculation(s.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    out.setTextColor(0xffeeeeee);
                                    stateText.setText("运算结束，耗时 " + (System.currentTimeMillis() - t) + " 毫秒");
                                    if (value[0].equals("表达式语法错误")) {
                                        out.setText("···");
                                    } else if (value[0].getBytes().length > 1000) {
                                        out.setText("数值太大，请长按此处显示结果");
                                    } else
                                        out.setText(value[0]);
                                    calcThread = null;
                                    rootValue = value[0];
                                }
                            });
                        }
                    });
                    calcThread.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!modified) {
                    return;
                }
                selection = editText.getSelectionEnd();
                modified = false;
                ForegroundColorSpan spans[] = s.getSpans(0, s.length(), ForegroundColorSpan.class);

                for (int n = spans.length; n-- > 0; )
                    s.removeSpan(spans[n]);
                for (Matcher m = Pattern.compile("[\\p{P}+^=÷×√]").matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xff81d4fa), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = keywords.matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xffa5d6a7), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editText.setText(s);
                editText.setSelection(selection);
                modified = true;
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setSubtitle("科学计算");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("关于").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AboutActivity.actionStart(context);
                return true;
            }
        });
        menu.add("退出").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                finish();
                return true;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
            return;
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.END))
                    drawer.closeDrawer(GravityCompat.END);
                if (drawer.isDrawerOpen(GravityCompat.START))
                    drawer.closeDrawer(GravityCompat.START);
                else
                    drawer.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }
}
