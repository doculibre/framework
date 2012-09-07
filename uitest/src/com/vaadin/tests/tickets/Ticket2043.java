package com.vaadin.tests.tickets;

import com.vaadin.LegacyApplication;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI.LegacyWindow;

public class Ticket2043 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        GridLayout layout = new GridLayout(10, 10);
        w.setContent(layout);
        createUI(layout);
    }

    private void createUI(GridLayout layout) {
        Link l = new Link("Vaadin home (new 200x200 window, no decor, icon)",
                new ExternalResource("http://www.vaadin.com"), "_blank", 200,
                200, Link.TARGET_BORDER_NONE);

        layout.addComponent(l);
    }
}
