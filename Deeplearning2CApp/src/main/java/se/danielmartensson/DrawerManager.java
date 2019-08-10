package se.danielmartensson;

import com.gluonhq.charm.down.Platform;
import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.LifecycleService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.control.NavigationDrawer.Item;
import com.gluonhq.charm.glisten.control.NavigationDrawer.ViewItem;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import static se.danielmartensson.Main.NEURALNETWORKS_VIEW;
import static se.danielmartensson.Main.LOADDATA_VIEW;
import static se.danielmartensson.Main.GLOBALCONFIGURATION_VIEW;
import static se.danielmartensson.Main.LAYERS_VIEW;
import static se.danielmartensson.Main.TRAINEVALGENERATE_VIEW;
import javafx.scene.image.Image;

public class DrawerManager {

    public static void buildDrawer(MobileApplication app) {
        NavigationDrawer drawer = app.getDrawer();
        
        NavigationDrawer.Header header = new NavigationDrawer.Header("Deeplearning2C",
                "C-Code generator",
                new Avatar(21, new Image(DrawerManager.class.getResourceAsStream("/icon.png"))));
        drawer.setHeader(header);
        
        final Item neuralnetworksItem = new ViewItem("Neural Networks", MaterialDesignIcon.HOME.graphic(), NEURALNETWORKS_VIEW, ViewStackPolicy.SKIP);
        final Item loaddataItem = new ViewItem("Load Data", MaterialDesignIcon.DASHBOARD.graphic(), LOADDATA_VIEW);
        final Item globalconfigurationItem = new ViewItem("Global Configuration", MaterialDesignIcon.DASHBOARD.graphic(), GLOBALCONFIGURATION_VIEW);
        final Item layersItem = new ViewItem("Layers", MaterialDesignIcon.DASHBOARD.graphic(), LAYERS_VIEW);
        final Item trainevalgenerateItem = new ViewItem("Train Eval Generate", MaterialDesignIcon.DASHBOARD.graphic(), TRAINEVALGENERATE_VIEW);

        drawer.getItems().addAll(neuralnetworksItem, loaddataItem, globalconfigurationItem, layersItem, trainevalgenerateItem);
        
        if (Platform.isDesktop()) {
            final Item quitItem = new Item("Quit", MaterialDesignIcon.EXIT_TO_APP.graphic());
            quitItem.selectedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
                }
            });
            drawer.getItems().add(quitItem);
        }
    }
}