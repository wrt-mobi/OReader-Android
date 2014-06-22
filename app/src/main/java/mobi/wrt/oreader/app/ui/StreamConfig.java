package mobi.wrt.oreader.app.ui;

import mobi.wrt.oreader.app.R;

public class StreamConfig {

    public static enum AdapterType {

        DEFAULT(R.layout.adapter_content1),
        FULL(R.layout.adapter_content2);

        private int layout;

        AdapterType(int adapterLayout) {
            layout = adapterLayout;
        }

        public int getLayout() {
            return layout;
        }
    }


}
