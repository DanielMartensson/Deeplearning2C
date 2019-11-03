package se.danielmartensson.deeplearning2c;

import static se.danielmartensson.deeplearning2c.Main.CONFIGURATIONS_VIEW;
import static se.danielmartensson.deeplearning2c.Main.DATA_VIEW;
import static se.danielmartensson.deeplearning2c.Main.MODELS_VIEW;
import static se.danielmartensson.deeplearning2c.Main.TRAINEVALGENERATE_VIEW;

import com.gluonhq.charm.down.Services;
import com.gluonhq.charm.down.plugins.LifecycleService;
import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.control.NavigationDrawer.Item;
import com.gluonhq.charm.glisten.control.NavigationDrawer.ViewItem;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

import javafx.scene.image.Image;
import se.danielmartensson.deeplearning2c.tools.Dialogs;

public class DrawerManager {
	
	private static Dialogs diaglogs = new Dialogs();

    public static void buildDrawer(MobileApplication app) {
        NavigationDrawer drawer = app.getDrawer();
        
        NavigationDrawer.Header header = new NavigationDrawer.Header("Deeplearning2C", "C-Code generator", new Avatar(40, new Image(DrawerManager.class.getResourceAsStream("/icon.png"))));
        drawer.setHeader(header);
        
        final Item modelsItem = new ViewItem("Models", MaterialDesignIcon.HOME.graphic(), MODELS_VIEW, ViewStackPolicy.SKIP);
        final Item configurationsItem = new ViewItem("Configurations", MaterialDesignIcon.DASHBOARD.graphic(), CONFIGURATIONS_VIEW);
        final Item dataItem = new ViewItem("Data", MaterialDesignIcon.DATE_RANGE.graphic(), DATA_VIEW);
        final Item trainEvalGenerateItem = new ViewItem("Train Eval Generate", MaterialDesignIcon.WORK.graphic(), TRAINEVALGENERATE_VIEW);

        drawer.getItems().addAll(modelsItem, configurationsItem, dataItem, trainEvalGenerateItem);
        
        if (true) { // Used to be Platform.isDesktop()
            final Item quitItem = new Item("Quit", MaterialDesignIcon.EXIT_TO_APP.graphic());
            quitItem.selectedProperty().addListener((obs, oldValue, newValue) -> {
            	if (newValue)
                	if(diaglogs.question("Quit", "Do you want to exit?") == true)
                		Services.get(LifecycleService.class).ifPresent(LifecycleService::shutdown);
                
            });
            drawer.getItems().add(quitItem);
        }
    }
}