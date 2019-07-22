package com.scurab.gwt.anuitor.client.ui;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.DataProvider.AsyncCallback;
import com.scurab.gwt.anuitor.client.util.PBarHelper;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

public class GroovyPage extends Composite {

    private static GroovyPageUiBinder uiBinder = GWT.create(GroovyPageUiBinder.class);

    interface GroovyPageUiBinder extends UiBinder<Widget, GroovyPage> {
    }

    @UiField
    VerticalPanel root;
    @UiField
    HTMLPanel code;
    @UiField
    TextArea result;
    @UiField
    Button executeCode;

    private final String ARG_SCREEN_INDEX = "_ScreenIndex_";
    private final String ARG_VIEW_POSITION = "_ViewPosition_";
    private final String CODE_TEMPLATE = "/*\n\tThere is no specific package context. Always **use full class names**!"
            + "\n\n\tFew tips:"
            + "\n\tdef viewId = G.id(\"R.id.button\") //get android ID value, don't use directly R.id.button"
            + "\n\tdef obj = G.field(myObject, \"mMyPrivateField\") //get reference to any field of object"
            + "\n\tdef view = new android.widget.TextView(activity) //create new TextView" + "\n\n*/"
            + "\n\n\ndef G = groovy.lang.GroovyHelper" + "\ndef rootView = G.getRootView(" + ARG_SCREEN_INDEX + ")"
            + "\ndef app = G.getApplication()"
            + "\ndef activities = G.getActivities()";

    private final String CODE_TEMPLATE_VIEW = "\ndef view = G.getView(" + ARG_SCREEN_INDEX + ", " + ARG_VIEW_POSITION + ")";

    private final int mScreenIndex;
    private final int mViewPosition;
    private final AceEditor mEditor = new AceEditor();
    private static final float CODE_RATIO = 0.65f;
    private static final DateTimeFormat DATE_TIME_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public GroovyPage(int screenIndex) {
        this(screenIndex, -1);
    }

    public GroovyPage(int screenIndex, int viewPosition) {
        initWidget(uiBinder.createAndBindUi(this));
        mScreenIndex = screenIndex;
        mViewPosition = viewPosition;

        code.add(mEditor);
        PBarHelper.show();
        startAceEditor("ace.js", "mode-groovy.js", "theme-tomorrow_night_bright.js", "ext-language_tools.js");

        onChangeSizes();
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                onChangeSizes();
            }
        });
        Element el = result.getElement();
        el.setAttribute("spellcheck", "false");
        el.setAttribute("readonly", "readonly");
    }

    private void startAceEditor(final String... files) {
        boolean done = true;
        for (int i = 0, n = files.length; i < n; i++) {
            final int index = i;
            String file = files[i];
            if (file != null) {
                file = "/anuitor/ace/" + file;                
                loadJsFile(file);
                files[index] = null;
                if (i == 0) {
                    done = false;
                    //all this timer nonsense is just because script injector doesn't work correctly, not sure how
                    //and we need to be sure that first file is loaded before any later import will fail :(
                    //FIXME: there must be some better way how to solve this issue
                    Timer timer = new Timer() {
                        @Override
                        public void run() {
                            startAceEditor(files);
                        }
                    };
                    timer.schedule(500);
                    break;
                }
            }
        }
        if (done) {
            Timer timer = new Timer() {
                @Override
                public void run() {
                    try {
                        mEditor.startEditor();
                        mEditor.setMode(AceEditorMode.GROOVY);
                        mEditor.setAutocompleteEnabled(true);
                        mEditor.setTheme(AceEditorTheme.TOMORROW_NIGHT_BRIGHT);
                        String template = CODE_TEMPLATE;
                        if (mViewPosition != -1) {
                            template += CODE_TEMPLATE_VIEW.replaceAll(ARG_VIEW_POSITION,
                                    Integer.toString(mViewPosition));
                        }
                        template = template.replaceAll(ARG_SCREEN_INDEX, Integer.toString(mScreenIndex));
                        mEditor.setText(template + "\n\n");
                        PBarHelper.hide();
                    } catch (Throwable t) {
                        schedule(500);
                    }
                }
            };
            timer.schedule(500);
        }
    }

    @UiHandler("clearResult")
    public void onClearResult(ClickEvent event) {
        result.setText("");
    }

    @UiHandler("executeCode")
    public void onExecuteCode(ClickEvent event) {
        String code = mEditor.getText();
        lockExecuteButton(true);
        PBarHelper.show();
        DataProvider.executeGroovyCode(code, new AsyncCallback<String>() {

            @Override
            public void onError(Request req, Response res, Throwable t) {
                Window.alert(t.getMessage());
                lockExecuteButton(false);
                PBarHelper.hide();
            }

            @Override
            public void onDownloaded(String response) {
                PBarHelper.hide();
                lockExecuteButton(false);
                String msg = "[" + DATE_TIME_FORMAT.format(new Date()) + "]\n" + response + "\n\n";
                result.setText(msg + result.getText());
            }
        });
    }

    private void lockExecuteButton(boolean lock) {
        executeCode.setEnabled(!lock);
        executeCode.setText(lock ? "Executing..." : "Execute");
    }

    private void onChangeSizes() {
        int height = Math.max(480, Window.getClientHeight());
        mEditor.setHeight((height * CODE_RATIO) + "px");
        result.setHeight((height * (1 - CODE_RATIO - 0.03f)) + "px");
    }

    private void loadJsFile(String path) {
        Element head = Document.get().getElementsByTagName("head").getItem(0);
        ScriptElement sce = Document.get().createScriptElement();
        sce.setType("text/javascript");
        sce.setSrc(path);
        head.appendChild(sce);
    }
}
