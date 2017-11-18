package com.sf.DarkCalculator;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.sf.ExpressionHandler.Constants;
import com.sf.ExpressionHandler.ExpressionHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity {

    public static MainActivity activity;
    private Context context;
    private Toolbar toolbar;
    private EditText inText;
    private TextView stateText;
    private TextView outText;
    private ViewPager drawerPager;
    private DrawerLayout drawer;
    private ArrayList<View> drawerPageList;
    public FrameLayout delete;

    private boolean realTime;

    private static final int[] YY = {1, 3, 1, 3, 3};
    private static final int[] ZZ = {6, 4, 5, 5, 5};

    private static final String[] OPERATOR = {"÷", "×", "-", "+", "%", ",", "i"};
    private static final String[] OPERATOR_VICE = {"√", "^", "!", "()", "°", "∞", "x"};

    private static final String[][] BUTTON = {
            {"sqrt", "cbrt", "root", "rand", "randInt", "lg", "ln", "log",
                    "abs", "min", "max", "fact", "sin", "cos", "tan", "asin", "acos",
                    "atan", "sinh", "cosh", "tanh", "asinh", "acosh", "atanh", "recipr",
                    "re", "im", "arg", "norm", "reg", "conj", "diff", "sum", "lim", "eval",
                    "fzero", "integ", "exp", "gcd", "lcm", "perm", "comb", "round", "floor",
                    "ceil", "sign", "gamma", "remn", "reduc", "prime", "isPrime", "isOdd",
                    "toDEG", "toRAD", "reStart", "setPrec", "setBase", "setCR", "setTS", "cust"},
            {"ans", "reg", "π", "e", "F", "h", "ћ", "γ", "φ", "c", "N", "R", "k", "G", "Φ", "me", "mn", "mp", "true", "false"}};

    private static final String[][] BUTTON_VICE = {
            {"平方根", "立方根", "开方", "随机复数", "随机整数", "常用对数", "自然对数", "对数",
                    "绝对值", "最小", "最大", "阶乘", "正弦", "余弦", "正切", "反正弦", "反余弦",
                    "反正切", "双曲正弦", "双曲余弦", "双曲正切", "反双曲正弦", "反双曲余弦",
                    "反双曲正切", "倒数", "实部", "虚部", "辐角", "范数", "寄存", "共轭复数",
                    "导函数", "累加求和", "极限", "求值", "函数零点", "定积分", "e底指数",
                    "最大公约", "最小公倍", "排列", "组合", "四舍五入", "向下取整", "向上取整",
                    "取正负号", "伽玛函数", "取余", "分数化简", "质数", "判断质数", "判断奇数",
                    "转角度", "转弧度", "重启APP", "输出精度", "输出进制", "排列方式", "字体大小", "自定义"}, {
            "上次运算", "寄存器", "圆周率", "自然底数", "法拉第", "普朗克", "约化普朗克", "欧拉", "黄金分割",
            "光速", "阿伏伽德罗", "理想气体", "玻尔兹曼", "万有引力", "磁通量子", "电子质量", "质子质量", "中子质量", "真", "假"}};

    private static final Pattern FUNCTIONS_KEYWORDS = Pattern.compile(
            "\\b(" + "sqrt|cbrt|root|rand|randInt|lg|ln|log" +
                    "abs|min|max|fact|sin|cos|tan|asin|acos" +
                    "atan|sinh|cosh|tanh|asinh|acosh|atanh|recipr" +
                    "re|im|arg|norm|reg|conj|diff|sum|lim|eval" +
                    "fzero|integ|exp|gcd|lcm|perm|comb|round|floor" +
                    "ceil|sign|gamma|remn|reduc|prime|isPrime|isOdd" +
                    "toDEG|toRAD|reStart|setPrec|setBase|setCR|setTS|cust" + ")\\b");

    private static final Pattern CONSTANS_KEYWORDS = Pattern.compile(
            "\\b(" + "ans|reg|π|e|F|h|ћ|γ|φ|c|N|R|k|G|Φ|me|mn|mp|true|false" + ")\\b");

    private static final String[] FUNCTION_LIST = {"科学计算", "大数计算", "时间计算", "进制转换",
            "方程式配平", "分子量计算", "亲戚关系计算", "大写数字", "汇率转换", "单位转换"};

    private static final String[] NUMERIC = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "·", "0", "=", "A", "B", "C", "D", "E", "F",
            "⑵", "⑶", "⑷", "⑸", "⑹", "⑺", "⑻", "⑼", "⑽", "⑾", "⑿", "⒀", "⒁", "⒂", "⒃"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();
        initEditText();
        initTextView();
        initDrawer();
        initPages();
        initTabs();
        initDelete();
        initSideBar();
        initNumeric();
        initOperator();
        initFunction();
        initConf();
    }

    private void initDelete() {
        delete = (FrameLayout) findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = inText.getText();
                int index = inText.getSelectionStart();
                int index2 = inText.getSelectionEnd();
                if (index == index2) {
                    if (index == 0) return;
                    editable.delete(index - 1, index);
                } else {
                    editable.delete(index, index2);
                }
            }
        });
        delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ExpressionHandler.stop();
                inText.setText(null);
                return true;
            }
        });
    }

    private void initConf() {
        for (int i = 0; i < YY.length; i++)
            barView.get(i).setNumColumns(preferences.getInt("CRy" + ("" + i), YY[i]));

        for (int i = 0; i < ZZ.length; i++)
            barAdapter.get(i).setValue(preferences.getInt("CRz" + ("" + i), ZZ[i]));

        realTime = preferences.getBoolean("real", true);
        findViewById(R.id.drawer_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });
    }

    private void initTextView() {
        stateText = (TextView) findViewById(R.id.text_state);
        stateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpressionHandler.stop();
                stateText.setText(null);
            }
        });
        outText = (TextView) findViewById(R.id.text_out);
        outText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outText.getText().toString().indexOf("重启") != -1) {
                    inText.setText("reStart()");
                    if (!realTime) new FastCalc("reStart()").start();
                    return;
                }
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(rootValue);
                Snackbar.make(v, "已复制运算结果", Snackbar.LENGTH_SHORT).show();
            }
        });
        outText.setOnLongClickListener(new View.OnLongClickListener() {
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
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        BigDecimalActivity.actionStart(context);
                        break;
                    case 3:
                        BaseConversionActivity.actionStart(context);
                        break;
                    case 7:
                        CapitalMoneyActivity.actionStart(context);
                        break;
                    default:
                        Snackbar.make(sideBar, "功能还未完善", Snackbar.LENGTH_SHORT).show();
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        GridViewAdapter sideBarAdapter = new GridViewAdapter(sideBar, Arrays.asList(FUNCTION_LIST),
                null, R.layout.button_sidebar);
        barAdapter.add(sideBarAdapter);
        sideBar.setAdapter(sideBarAdapter);
    }

    private void initDrawer() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_main);
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
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                } else {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN, GravityCompat.END);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private ArrayList<GridView> barView = new ArrayList<>();
    private ArrayList<GridViewAdapter> barAdapter = new ArrayList<>();

    public void setBarCR(final int x, final int y, final int z) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("CRy" + ("" + x), y)
                .putInt("CRz" + ("" + x), z)
                .apply();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                barView.get(x).setNumColumns(y);
                barAdapter.get(x).setValue(z);
            }
        });
    }


    private void initFunction() {
        int i = 0;
        for (View view : drawerPageList) {
            GridView operatorProBar = (GridView) view;
            barView.add(operatorProBar);

            if (i == 0) {
                operatorProBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyInText(BUTTON[0][position] + "()");
                        inText.setSelection(inText.getSelectionStart() - 1);
                    }
                });

                operatorProBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String text = BUTTON[0][position];
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle(text);
                        dialog.setMessage(HelpUtil.getFunctionHelp(text));
                        dialog.setPositiveButton("确定", null);
                        dialog.show();
                        return true;
                    }
                });
            } else {
                operatorProBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        modifyInText(BUTTON[1][position]);
                    }
                });

                operatorProBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        String text = BUTTON[1][position];
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setTitle(text);
                        dialog.setMessage(HelpUtil.getFunctionHelp(text));
                        dialog.setPositiveButton("确定", null);
                        dialog.show();
                        return true;
                    }
                });
            }
            int id = i == 0 ? R.layout.button_function : R.layout.button_constant;
            GridViewAdapter operatorProAdapter = new GridViewAdapter(operatorProBar,
                    Arrays.asList(BUTTON[i++]), Arrays.asList(BUTTON_VICE[i - 1]), id);

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
                modifyInText(OPERATOR[position]);
            }
        });
        operatorBar.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                modifyInText(OPERATOR_VICE[position]);
                return true;
            }
        });
        GridViewAdapter operatorAdapter = new GridViewAdapter(operatorBar, Arrays.asList(OPERATOR),
                Arrays.asList(OPERATOR_VICE), R.layout.button_operator);
        barAdapter.add(operatorAdapter);
        operatorBar.setAdapter(operatorAdapter);
    }

    private void initNumeric() {
        GridView numericBar = (GridView) findViewById(R.id.bar_numeric);
        barView.add(numericBar);
        numericBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = position == 9 ? "." : NUMERIC[position];
                if (str.equals("=")) {
                    if (calcThread != null) {
                        Snackbar.make(view, "请等待当前运算完成", Snackbar.LENGTH_SHORT)
                                .setAction("停止运算", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ExpressionHandler.stop();
                                    }
                                }).show();
                        return;
                    }
                    outText.setTextColor(0xffbdbdbd);
                    stateText.setText("运算中...");
                    calcThread = new Calc(inText.getText().toString());
                    calcThread.start();
                    return;
                }
                modifyInText(str);
            }
        });
        GridViewAdapter numericAdapter = new GridViewAdapter(numericBar, Arrays.asList(NUMERIC),
                null, R.layout.button_numeric);
        barAdapter.add(numericAdapter);
        numericBar.setAdapter(numericAdapter);
    }

    private void modifyInText(String str) {
        int index = inText.getSelectionStart();
        int index2 = inText.getSelectionEnd();
        if (index == index2) {
            inText.getText().insert(index, str);
        } else {
            inText.getText().replace(index, index2, str);
        }
    }

    class FastCalc extends Thread implements Runnable {
        private String exp;

        public FastCalc(String exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            final long t = System.currentTimeMillis();
            final String[] value = ExpressionHandler.calculation(exp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    outText.setTextColor(0xffbdbdbd);
                    stateText.setText("运算结束，耗时 " + (System.currentTimeMillis() - t) + " 毫秒");
                    if (value[0].getBytes().length > 1000) {
                        outText.setText("数值太大，请长按此处显示结果");
                    } else
                        outText.setText(value[0]);
                    rootValue = value[0];
                    calcThread = null;
                }
            });
        }
    }

    class Calc extends Thread implements Runnable {
        private String exp;

        public Calc(String exp) {
            this.exp = exp;
        }

        @Override
        public void run() {
            final long t = System.currentTimeMillis();
            final String[] value = ExpressionHandler.calculation(exp);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stateText.setText("运算结束，耗时 " + (System.currentTimeMillis() - t) + " 毫秒");
                    if (value[1].equals("true")) {
                        outText.setTextColor(0xffff4081);
                        outText.setText(value[0]);
                    } else {
                        Constants.setAns(value[0]);
                        if (value[0].getBytes().length > 1000) {
                            outText.setText("数值太大，请长按此处显示结果");
                            ResultsActivity.actionStart(context, value[0]);
                        } else
                            outText.setText(value[0]);
                    }
                    rootValue = value[0];
                    calcThread = null;
                }
            });
        }

    }

    private boolean modified = true;
    private int selection = 0;
    private Thread calcThread;
    private String rootValue;

    private void initEditText() {
        inText = (EditText) findViewById(R.id.editText);
        AutofitHelper.create(inText).setMinTextSize(28);
        inText.requestFocus();
        inText.requestFocusFromTouch();

        inText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    if (calcThread == null)
                        stateText.setText(null);
                    outText.setTextColor(0xffbdbdbd);
                    outText.setText(null);
                    rootValue = null;
                    return;
                }
                if (!realTime) outText.setText(null);
                if (calcThread == null && realTime) {
                    stateText.setText("运算中...");
                    calcThread = new FastCalc(s.toString());
                    calcThread.start();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!modified) return;

                selection = inText.getSelectionStart();
                ForegroundColorSpan spans[] = s.getSpans(0, s.length(), ForegroundColorSpan.class);
                for (int n = spans.length; n-- > 0; )
                    s.removeSpan(spans[n]);

                for (Matcher m = Pattern.compile("[\\p{P}+^=÷×√]").matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xff81d4fa), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = Pattern.compile("[∞xi°]").matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xfff48fb1), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = FUNCTIONS_KEYWORDS.matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xffa5d6a7), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                for (Matcher m = CONSTANS_KEYWORDS.matcher(s); m.find(); )
                    s.setSpan(new ForegroundColorSpan(0xfffff59d), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                modified = false;
                inText.setText(s);
                modified = true;
                inText.setSelection(selection);
            }
        });
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        toolbar.setSubtitle("科学计算");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    private void setGodMode(boolean isGodMode) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        ActionBar actionBar = getSupportActionBar();
        godMenuItem.setChecked(isGodMode);
        if (isGodMode) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            drawer.setVisibility(View.GONE);
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(inText, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            imm.showSoftInput(inText, InputMethodManager.SHOW_FORCED);
        } else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            drawer.setVisibility(View.VISIBLE);
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(inText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            imm.hideSoftInputFromWindow(inText.getWindowToken(), 0);
        }
    }

    private MenuItem godMenuItem;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean isGodMode = preferences.getBoolean("godMode", false);
        godMenuItem = menu.add("上帝输入").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                boolean isGodMode = !item.isChecked();
                preferences.edit().putBoolean("godMode", isGodMode).apply();
                setGodMode(isGodMode);
                return true;
            }
        }).setCheckable(true).setChecked(isGodMode);
        setGodMode(isGodMode);

        menu.add("帮助").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("帮助")
                        .setMessage(R.string.app_help)
                        .setPositiveButton("确定", null)
                        .show();
                return true;
            }
        });
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
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
            drawer.closeDrawer(GravityCompat.END);
            return;
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.END);
                    drawer.closeDrawer(GravityCompat.END);
                }
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
