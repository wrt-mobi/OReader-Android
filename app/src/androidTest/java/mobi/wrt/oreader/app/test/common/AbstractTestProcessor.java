package mobi.wrt.oreader.app.test.common;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.test.ApplicationTestCase;

import java.io.InputStream;

import by.istin.android.xcore.callable.ISuccess;
import by.istin.android.xcore.fragment.XListFragment;
import by.istin.android.xcore.model.CursorModel;
import by.istin.android.xcore.processor.IProcessor;
import by.istin.android.xcore.provider.ModelContract;
import by.istin.android.xcore.source.DataSourceRequest;
import by.istin.android.xcore.utils.AppUtils;
import by.istin.android.xcore.utils.CursorUtils;
import by.istin.android.xcore.utils.StringUtil;
import mobi.wrt.oreader.app.application.Application;

public class AbstractTestProcessor extends ApplicationTestCase<Application> {

    private TestDataSource testDataSource;

    public AbstractTestProcessor() {
        super(Application.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        try {
            createApplication();
        } catch (NullPointerException e) {

        }
        testDataSource = new TestDataSource();
    }

    public void clear(Class<?> ... entities) {
        for (Class<?> entity : entities) {
            getApplication().getContentResolver().delete(ModelContract.getUri(entity), null, null);
        }
    }

    protected void checkRequiredFields(Class<?> classEntity, String ... fields) {
        Uri uri = ModelContract.getUri(classEntity);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        checkRequiredFields(uri, projection, selection, selectionArgs, sortOrder, fields);
    }

    protected void checkRequiredFields(XListFragment fragment, Bundle args, String ... fields) {
        fragment.setArguments(args);
        checkRequiredFields(fragment.getUri(), fragment.getProjection(), fragment.getSelection(), fragment.getSelectionArgs(), fragment.getOrder(), fields);
    }

    protected void checkRequiredFields(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, String... fields) {
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        CursorModel cursorModel = new CursorModel(cursor);
        for (int i = 0; i < cursorModel.size(); i++) {
            CursorModel entity = cursorModel.get(i);
            for (int j = 0; j < fields.length; j++) {
                String field = fields[j];
                String value = entity.getString(field);
                assertNotNull(field+ " is required",value);
                assertFalse(field+ " is required", StringUtil.isEmpty(value));
            }
        }
        CursorUtils.close(cursor);
    }

    protected void checkCount(XListFragment fragment, Bundle args, int count) {
        fragment.setArguments(args);
        checkCount(count, fragment.getUri(), fragment.getProjection(), fragment.getSelection(), fragment.getSelectionArgs(), fragment.getOrder());
    }

    protected void checkCount(Class<?> entity, int count) {
        Uri uri = ModelContract.getUri(entity);
        String[] projection = null;
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null;
        checkCount(count, uri, projection, selection, selectionArgs, sortOrder);
    }

    protected void checkCount(int count, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        checkCount(count, uri, projection, selection, selectionArgs, sortOrder, null);
    }

    protected void checkCount(int count, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, ISuccess<Cursor> success) {
        Cursor cursor = getApplication().getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (CursorUtils.isEmpty(cursor)) {
            assertTrue("result is empty", count == 0);
        } else {
            assertEquals(count, cursor.getCount());
        }
        if (success != null) {
            success.success(cursor);
        }
        CursorUtils.close(cursor);
    }

    public Object testExecute(String processorKey, String feedUri) throws Exception {
        return testExecute(getApplication(), processorKey, feedUri);
    }

    public Object testExecute(Context context, String processorKey, String feedUri) throws Exception {
        IProcessor processor = (IProcessor) AppUtils.get(context, processorKey);
        DataSourceRequest dataSourceRequest = new DataSourceRequest(feedUri);
        InputStream inputStream = testDataSource.getSource(dataSourceRequest);
        return processor.execute(dataSourceRequest, testDataSource, inputStream);
    }
}
