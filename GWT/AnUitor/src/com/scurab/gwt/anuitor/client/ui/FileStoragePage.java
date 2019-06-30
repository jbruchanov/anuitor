package com.scurab.gwt.anuitor.client.ui;

import java.util.Stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;
import com.scurab.gwt.anuitor.client.DataProvider;
import com.scurab.gwt.anuitor.client.model.FSItemJSO;
import com.scurab.gwt.anuitor.client.util.TableTools;

public class FileStoragePage extends Composite {

    private static StoragePageUiBinder uiBinder = GWT.create(StoragePageUiBinder.class);

    interface StoragePageUiBinder extends UiBinder<Widget, FileStoragePage> {
    }

    @UiField(provided = true) CellTable<FSItemJSO> cellTable = new CellTable<FSItemJSO>();
    @UiField Label currentFolder;
    
    private Stack<String> mFolders = new Stack<String>();

    public FileStoragePage() {
        initWidget(uiBinder.createAndBindUi(this));
        initTable();
        bind();
        loadData("");
    }

    private void initTable() {
        TableTools.initTableForFileStorage(cellTable);
    }

    private void loadData(final String folder) {        
        DataProvider.getFiles(folder, new DataProvider.AsyncCallback<JsArray<FSItemJSO>>() {

            @Override
            public void onDownloaded(JsArray<FSItemJSO> result) {
                onDataLoaded(folder, result);
            }

            @Override
            public void onError(Request req, Response res, Throwable t) {
                Window.alert(t.getMessage());
            }
        });
    }

    private void bind() {
        final SingleSelectionModel<FSItemJSO> ssm = new SingleSelectionModel<FSItemJSO>();
        cellTable.setSelectionModel(ssm);
        ssm.addSelectionChangeHandler(new Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                onFileClick(ssm.getSelectedObject());
            }
        });
    }

    public void onFileClick(FSItemJSO item) {
        switch (item.getType()) {
            case FSItemJSO.TYPE_FOLDER:
                loadData(mFolders.size() == 1 ? item.getName() : mFolders.peek() + "/" + item.getName());
                break;
            case FSItemJSO.TYPE_FILE:
                String url = Window.Location.getProtocol() + "//" + Window.Location.getHost() + "/storage.json?path=" + mFolders.peek() + "/" + item.getName();
                Window.open(url, "_blank", "");
                break;
            case FSItemJSO.TYPE_PARENT_FOLDER:
                mFolders.pop();//remove current folder
                loadData(mFolders.pop());//load current and remove it, onLoad will be used                                
                break;
        }
    }

    protected void onDataLoaded(String currentFolder, JsArray<FSItemJSO> result) {                
        mFolders.push(currentFolder);  
        this.currentFolder.setText(currentFolder);
        ListDataProvider<FSItemJSO> createDataProvider = TableTools.createDataProvider(result, mFolders.size() > 1);
        createDataProvider.addDataDisplay(cellTable);
    } 
}
