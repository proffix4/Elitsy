package kz.proffix4.elitsy;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.webkit.*;
import android.widget.*;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    EditText urlText, searchText;
    TextView searchCountText;
    ImageButton backButton, forwardButton, refreshButton, loadButton, forwardSearchButton, searchCloseButton, searchBackButton, homeButton;
    ImageButton searchButton, shareButton;
    RelativeLayout mainToolLayout, searchToolLayout, mainLayout;
    TextView loadingTextView;
    MyViewClient myViewClient = new MyViewClient();

    final int MAX_100 = 100;
    final int RCODE_SAVE_IMAGE = 1;
    final String html_message1 = "data:text/html;charset=utf-8;base64,";
    final String html_message2 = "about:blank";
    final int ID_SAVEIMAGE = 1;
    final int ID_SHARELINK = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlText = (EditText) findViewById(R.id.urlText);
        loadButton = (ImageButton) findViewById(R.id.loadButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        refreshButton = (ImageButton) findViewById(R.id.refreshButton);
        forwardButton = (ImageButton) findViewById(R.id.forwardButton);
        homeButton = (ImageButton) findViewById(R.id.homeButton);
        searchButton = (ImageButton) findViewById(R.id.searchButton);
        shareButton = (ImageButton) findViewById(R.id.shareButton);
        mainToolLayout = (RelativeLayout) findViewById(R.id.mainToolLayout);
        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        searchToolLayout = (RelativeLayout) findViewById(R.id.searchToolLayout);
        forwardSearchButton = (ImageButton) findViewById(R.id.forwardSearchButton);
        searchBackButton = (ImageButton) findViewById(R.id.searchBackButton);
        searchCloseButton = (ImageButton) findViewById(R.id.searchCloseButton);
        searchText = (EditText) findViewById(R.id.searchText);
        searchCountText = (TextView) findViewById(R.id.searchCountText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webView);
        loadingTextView = (TextView) findViewById(R.id.loadingTextView);

        searchToolLayout.setVisibility(View.GONE);
        progressBar.setMax(MAX_100);
        progressBar.setVisibility(View.GONE);

        urlText.setFocusable(false);
        urlText.setFocusableInTouchMode(false);
        urlText.setTextIsSelectable(true);
        urlText.setHighlightColor(Color.MAGENTA);

        webView.requestFocus();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        webView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        mainLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        loadingTextView.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        webView.setWebViewClient(myViewClient);

        // Обработчик отображения загрузки страниц
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress < MAX_100 && progressBar.getVisibility() == ProgressBar.GONE) {
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                }
                if (progress == MAX_100) {
                    progressBar.setVisibility(ProgressBar.GONE);
                }
            }
        });

        // Добавление контекстного меню к webView
        registerForContextMenu(webView);

        // Восстановление состояния просмотра (страницы) в webView
        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl(getString(R.string.home_page));
        }

        // Обработчик кнопки загрузки страницы
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Скрываем клавиатуру
                hideSoftInput();
                // Загружаем страниу в webView
                webView.loadUrl(urlText.getText().toString());
            }
        });

        // Обработчик кнопки Назад
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });

        // Обработчик кнопки Вперед
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoForward()) {
                    webView.goForward();
                }
            }
        });

        // Обработчик кнопки Обновить
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.reload();
            }
        });

        // Обработчик кнопки Домой
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Скрываем клавиатуру
                hideSoftInput();
                // Загружаем страниу в webView
                webView.loadUrl(getString(R.string.home_page));
                // Показываем адрес домашней страницы
                urlText.setText(getString(R.string.home_page));
            }
        });

        // Обработчик кнопки Поиск
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressBar.getVisibility() == ProgressBar.GONE) {
                    searchCountText.setText("");
                    mainToolLayout.setVisibility(View.GONE);
                    searchToolLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // Обработчик кнопки Поиск Вперед
        forwardSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(true);
            }
        });

        // Обработчик кнопки Поиск Назад
        searchBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.findNext(false);
            }
        });

        // Обработчик нажатий кнопок в окошке поиска
        searchText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // Если нажата клавиша Enter
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))) {
                    // Скрываем клавиатуру
                    hideSoftInput();
                    // Ищем нужный текст в webView
                    webView.findAll(searchText.getText().toString());
                    // Активируем возможность отображения найденного теккста в webView
                    try {
                        Method m = WebView.class.getMethod("setFindIsUp", Boolean.TYPE);
                        m.invoke(webView, true);
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });

        // Обработчик поиска в WebView
        webView.setFindListener(new WebView.FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                searchCountText.setText("");
                if (numberOfMatches > 0) {
                    searchCountText.setText(String.format("%d %s %d", activeMatchOrdinal + 1, getString(R.string.iz), numberOfMatches));
                } else {
                    searchCountText.setText(R.string.ne_naideno);
                }
            }
        });

        // Обработчик кнопки закрытия поиска
        searchCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.clearMatches();
                searchText.setText("");
                mainToolLayout.setVisibility(View.VISIBLE);
                searchToolLayout.setVisibility(View.GONE);
                hideSoftInput();
            }
        });


        // Обработчик кнопки Поделиться
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = webView.getUrl();
                if (msg.equals(html_message1) || (msg.equals(html_message2))) return;
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.podelitsa)));
            }
        });
    }

    // Скрываем клавиатуру
    private void hideSoftInput() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    // Класс собственного загрузчика html
    public class MyViewClient extends WebViewClient {
        // Переопределение метода загрузки страницы
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            urlText.setText(url);
            loadingTextView.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            CookieManager.getInstance().setAcceptCookie(true);
            view.setVisibility(View.GONE);
            return true;
        }

        // Переопределение метода окончания загрузки страницы
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String msg = webView.getUrl();
            if (!msg.equals(html_message1) && !(msg.equals(html_message2))) {
                loadingTextView.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
            }
            urlText.setText(webView.getUrl());
        }

        // Переопределение метода начала загрузки страницы
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);
            super.onPageStarted(view, url, favicon);
            String msg = url;
            if (!msg.equals(html_message1) && !(msg.equals(html_message2))) {
                loadingTextView.setVisibility(View.VISIBLE);
                view.setVisibility(View.GONE);
            }
        }

        // Переопределение метода ошибки загрузки страницы
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            loadingTextView.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            String msg = "<html><body style='background: black;'>" +
                    "<p style='color: red;text-align:  center;margin-top: 10%';>" +
                    "<big><big><big><big><big>" + getString(R.string.net_dostupa_k_internet) + "</big></big></big></big></big></p></body></html>";
            view.loadDataWithBaseURL(null, msg, "text/html", "UTF-8", null);
        }

    }

    // Обработчик системной кнопки назад
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
        }
    }

    // Обработчик записи состояния активности перед поворотом
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    // Обработчики контекстного меню
    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View view, final ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(menu, view, contextMenuInfo);

        final WebView.HitTestResult result = webView.getHitTestResult();

        // Обработчик выбора пунктов контекстного меню
        MenuItem.OnMenuItemClickListener handler = new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                // Обработчик меню для картинки
                if (item.getItemId() == ID_SAVEIMAGE) {
                    saveImage();
                    return true;
                }
                // Обработчик меню для ссылки
                if (item.getItemId() == ID_SHARELINK) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, result.getExtra());
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, getString(R.string.podelitsa)));
                    return true;
                }
                return false;
            }

        };

            // Создание меню, вызванного для картинки
        if (result.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            // menu.setHeaderTitle(result.getExtra());
            menu.add(0, ID_SAVEIMAGE, 0, R.string.sohranit_izobr).setOnMenuItemClickListener(handler);
            // Создание меню, вызванного для ссылки
        } else if (result.getType() == WebView.HitTestResult.ANCHOR_TYPE ||
                result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
            //  menu.setHeaderTitle(result.getExtra());
            menu.add(0, ID_SHARELINK, 0, R.string.podelitsa).setOnMenuItemClickListener(handler);
        }

    }


    // Сохранение картинки
    private void saveImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RCODE_SAVE_IMAGE);
        } else {
            final WebView.HitTestResult webViewHitTestResult = webView.getHitTestResult();
            if (webViewHitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                    webViewHitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                String DownloadImageURL = webViewHitTestResult.getExtra();
                if (URLUtil.isValidUrl(DownloadImageURL)) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadImageURL));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, Uri.parse(DownloadImageURL).getLastPathSegment());
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);
                    Toast.makeText(MainActivity.this, R.string.izobr_sohraneno, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, R.string.oshibka_sohr, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    // Вызывается после установки разрешения
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RCODE_SAVE_IMAGE) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            }
        }
    }

}
