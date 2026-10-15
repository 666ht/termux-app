package com.termux.app.api.file;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;

import com.termux.app.api.file.FileReceiverActivity;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class FileReceiverActivityTest {

    @Test
    public void testIsSharedTextAnUrl() {
        List<String> validUrls = new ArrayList<>();
        validUrls.add("http://example.com");
        validUrls.add("https://example.com");
        validUrls.add("https://example.com/path/parameter=foo");
        validUrls.add("magnet:?xt=urn:btih:d540fc48eb12f2833163eed6421d449dd8f1ce1f&dn=Ubuntu+desktop+19.04+%2864bit%29&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.ccc.de%3A80");
        for (String url : validUrls) {
            Assert.assertTrue(FileReceiverActivity.isSharedTextAnUrl(url));
        }

        List<String> invalidUrls = new ArrayList<>();
        invalidUrls.add("a test with example.com");
        invalidUrls.add("");
        invalidUrls.add(null);
        for (String url : invalidUrls) {
            Assert.assertFalse(FileReceiverActivity.isSharedTextAnUrl(url));
        }
    }


    @Test
    public void testIncomingUriFromIntentData() {
        Uri uri = Uri.parse("content://example.test/files/from-data.txt");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://example.test/files/from-extra.txt"));

        Assert.assertEquals(uri, FileReceiverActivity.getIncomingUri(intent, false));
    }

    @Test
    public void testIncomingUriFallsBackToStreamExtra() {
        Uri uri = Uri.parse("content://example.test/files/from-extra.txt");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra(Intent.EXTRA_STREAM, uri);

        Assert.assertEquals(uri, FileReceiverActivity.getIncomingUri(intent, false));
    }

    @Test
    public void testIncomingUriFallsBackToClipData() {
        Uri uri = Uri.parse("content://example.test/files/from-clip.txt");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClipData(new ClipData("shared-file",
            new String[]{"application/octet-stream"}, new ClipData.Item(uri)));

        Assert.assertEquals(uri, FileReceiverActivity.getIncomingUri(intent, false));
    }

    @Test
    public void testIncomingUriHandlesSendMultipleExtra() {
        Uri first = Uri.parse("content://example.test/files/first.txt");
        Uri second = Uri.parse("content://example.test/files/second.txt");
        java.util.ArrayList<Uri> items = new java.util.ArrayList<>();
        items.add(first);
        items.add(second);
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, items);

        Assert.assertEquals(first, FileReceiverActivity.getIncomingUri(intent, true));
    }

    @Test
    public void testIncomingUriReturnsNullForEmptyIntent() {
        Assert.assertNull(FileReceiverActivity.getIncomingUri(new Intent(Intent.ACTION_VIEW), false));
        Assert.assertNull(FileReceiverActivity.getIncomingUri(null, false));
    }

}
