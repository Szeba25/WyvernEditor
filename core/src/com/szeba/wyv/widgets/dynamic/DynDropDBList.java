package com.szeba.wyv.widgets.dynamic;

import com.szeba.wyv.Wyvern;
import com.szeba.wyv.data.ListElement;
import com.szeba.wyv.data.Signal;
import com.szeba.wyv.utilities.CommandStringGen;
import com.szeba.wyv.utilities.Separator;
import com.szeba.wyv.utilities.StringUtilities;
import com.szeba.wyv.widgets.ext.list.DropList;

import java.util.ArrayList;

public class DynDropDBList extends DropList implements Dynamic {

    private String receiver;
    private String entry;

    public DynDropDBList(int ox, int oy, int rx, int ry, int w, int hval, String entry) {
        super(ox, oy, rx, ry, w, hval, null);

        this.entry = entry;

        this.dynReset();
    }

    @Override
    public void dynSetReceiver(String receiver) {
        this.receiver = receiver;
    }

    @Override
    public String dynGetReceiver() {
        return receiver;
    }

    @Override
    public void dynProcessSignal(Signal signal) {
        setVisible(true);
    }

    @Override
    public void dynSetValue(String value) {

    }

    @Override
    public String dynGetValue() {
        return null;
    }

    @Override
    public void dynReset() {
        // List one database entry
        ArrayList<ListElement> elearr = new ArrayList<ListElement>();

        if (Wyvern.database.ent.entryData.get(entry) == null) {
            this.resetElements();
        } else {
            ArrayList<String> arr = Wyvern.database.ent.entryData.get(entry).getItems();

            for (int x = 0; x < arr.size(); x++) {

                // We must handle non ascii texts.
                String dataArr = StringUtilities.safeSplit(arr.get(x), Separator.dataUnit)[0];
                if (CommandStringGen.isArrayText(dataArr)) {
                    dataArr = CommandStringGen.generateArrayText(dataArr);
                }

                elearr.add(new ListElement(Integer.toString(x) + ": "
                        + dataArr,
                        Integer.toString(x)));
            }

            this.setElements(elearr);
        }
    }

    @Override
    public void processClickedElement(int id) {
        super.processClickedElement(id);
        this.setSignal(new Signal(Signal.T_DROPLIST, this.entry + ": " + this.getElement(id).getOriginalName()));
        this.setVisible(false);
    }

    @Override
    public String dynGetCommandStringFormatter(String data) {
        return data;
    }
}
