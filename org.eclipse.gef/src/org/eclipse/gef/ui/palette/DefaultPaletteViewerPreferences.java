package org.eclipse.gef.ui.palette;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import org.eclipse.gef.GEFPlugin;

import org.eclipse.swt.graphics.FontData;

/**
 * This is the default implementation for PaletteViewerPreferences.  It uses
 * a single IPreferenceStore to load and save the palette viewer settings.
 * 
 * @author Pratik Shah
 */
public class DefaultPaletteViewerPreferences
	implements PaletteViewerPreferences
{

private PreferenceStoreListener listener;
private IPropertyChangeListener fontListener;
private FontData fontData;
private PropertyChangeSupport listeners = new PropertyChangeSupport(this);
private IPreferenceStore store;
private static final int DEFAULT_PALETTE_SIZE = 150;

public DefaultPaletteViewerPreferences() {
	this(GEFPlugin.getDefault().getPreferenceStore());
}

/**
 * Constructor
 * 
 * @param	store	The IPreferenceStore where the settings are stored.
 */
public DefaultPaletteViewerPreferences(final IPreferenceStore store) {
	this.store = store;
	store.setDefault(PREFERENCE_PALETTE_SIZE, DEFAULT_PALETTE_SIZE);
	store.setDefault(PREFERENCE_DETAILS_ICON_SIZE, false);
	store.setDefault(PREFERENCE_FOLDER_ICON_SIZE, true);
	store.setDefault(PREFERENCE_ICONS_ICON_SIZE, true);
	store.setDefault(PREFERENCE_LIST_ICON_SIZE, false);
	store.setDefault(PREFERENCE_LAYOUT, LAYOUT_LIST);
	store.setDefault(PREFERENCE_AUTO_COLLAPSE, COLLAPSE_AS_NEEDED);
	store.setDefault(PREFERENCE_FONT, JFaceResources.getDialogFont().
			getFontData()[0].toString());

	listener = new PreferenceStoreListener();
	store.addPropertyChangeListener(listener);
	
	fontListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (JFaceResources.DIALOG_FONT.equals(event.getProperty())) {
				FontData data = JFaceResources.getDialogFont().getFontData()[0];
				// We need to set the font data first because that will cause a property 
				// change event to be fired.
				if (getFontData().toString().equals(store.getDefaultString(PREFERENCE_FONT))) {
					setFontData(data);
				}
				store.setDefault(PREFERENCE_FONT, data.toString());
			}
		}
	};
	JFaceResources.getFontRegistry().addListener(fontListener);
}

/**
 * NOTE: The <code>oldValue</code> field of the <code>PropertyChangeEvent</code> used to
 * notify listeners will always be <code>null</code>.
 * 
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#addPropertyChangeListener(PropertyChangeListener)
 */
public void addPropertyChangeListener(PropertyChangeListener listener) {
	listeners.addPropertyChangeListener(listener);
}

/**
 * Converts the given layout code to the matching preference name.
 * 
 * <UL>
 * <LI> int <-> String </LI>
 * <LI> LAYOUT_LIST <-> PREFERENCE_LIST_ICON_SIZE </LI>
 * <LI> LAYOUT_FOLDER <-> PREFERENCE_FOLDER_ICON_SIZE </LI>
 * <LI> LAYOUT_ICONS <-> PREFERENCE_ICONS_ICON_SIZE </LI>
 * <LI> LAYOUT_DETAILS <-> PREFERENCE_DETAILS_ICON_SIZE </LI>
 * </UL>
 * 
 * @param layout	LAYOUT_LIST, LAYOUT_DETAILS, LAYOUT_FOLDER, or LAYOUT_ICONS
 * @return	The corresponding preference String
 */
protected String convertLayoutToPreferenceName(int layout) {
	String key = ""; //$NON-NLS-1$
	switch (layout) {
		case LAYOUT_FOLDER :
			key = PREFERENCE_FOLDER_ICON_SIZE;
			break;
		case LAYOUT_LIST :
			key = PREFERENCE_LIST_ICON_SIZE;
			break;
		case LAYOUT_ICONS :
			key = PREFERENCE_ICONS_ICON_SIZE;
			break;
		case LAYOUT_DETAILS :
			key = PREFERENCE_DETAILS_ICON_SIZE;
			break;
	}
	return key;	
}

/**
 * Converts the given preference to the matching layout code.
 * 
 * <UL>
 * <LI> int <-> String </LI>
 * <LI> LAYOUT_LIST <-> PREFERENCE_LIST_ICON_SIZE </LI>
 * <LI> LAYOUT_FOLDER <-> PREFERENCE_FOLDER_ICON_SIZE </LI>
 * <LI> LAYOUT_ICONS <-> PREFERENCE_ICONS_ICON_SIZE </LI>
 * <LI> LAYOUT_DETAILS <-> PREFERENCE_DETAILS_ICON_SIZE </LI>
 * </UL>
 * 
 * @param preference	PREFERENCE_DETAILS_ICON_SIZE, PREFERENCE_FOLDER_ICON_SIZE,
 * 						PREFERENCE_ICONS_ICON_SIZE or PREFERENCE_LIST_ICON_SIZE
 * @return	The corresponding layout code
 */
protected int convertPreferenceNameToLayout(String preference) {
	int layout = -1;
	if (preference.equals(PREFERENCE_DETAILS_ICON_SIZE)) {
		layout = LAYOUT_DETAILS;
	} else if (preference.equals(PREFERENCE_FOLDER_ICON_SIZE)) {
		layout = LAYOUT_FOLDER;
	} else if (preference.equals(PREFERENCE_ICONS_ICON_SIZE)) {
		layout = LAYOUT_ICONS;
	} else if (preference.equals(PREFERENCE_LIST_ICON_SIZE)) {
		layout = LAYOUT_LIST;
	}
	return layout;
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#dispose()
 */
public void dispose() {
	store.removePropertyChangeListener(listener);
	JFaceResources.getFontRegistry().removeListener(fontListener);
}

/**
 * The oldValue of the PropertyChangeEvent that is fired will always be <code>null</code>.
 * 
 * @see java.beans.PropertyChangeSupport#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
 */
protected void firePropertyChanged(String property, Object newVal) {
	listeners.firePropertyChange(property, null, newVal);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getAutoCollapseSetting()
 */
public int getAutoCollapseSetting() {
	return store.getInt(PREFERENCE_AUTO_COLLAPSE);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getFontData()
 */
public FontData getFontData() {
	if (fontData == null) {
		fontData = new FontData(store.getString(PREFERENCE_FONT));
	}
	return fontData;
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getLayoutSetting()
 */
public int getLayoutSetting() {
	return store.getInt(PREFERENCE_LAYOUT);
}

/** * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#getPaletteSize() */
public int getPaletteSize() {
	return store.getInt(PREFERENCE_PALETTE_SIZE);
}

/** * @return The IPreferenceStore used by this class to store the preferences. */
protected IPreferenceStore getPreferenceStore() {
	return store;
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#removePropertyChangeListener(PropertyChangeListener)
 */
public void removePropertyChangeListener(PropertyChangeListener listener) {
	listeners.removePropertyChangeListener(listener);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setAutoCollapseSetting(int)
 */
public void setAutoCollapseSetting(int newVal) {
	store.setValue(PREFERENCE_AUTO_COLLAPSE, newVal);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setFontData(FontData)
 */
public void setFontData(FontData data) {
	fontData = data;
	store.setValue(PREFERENCE_FONT, data.toString());
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setLayoutSetting(int)
 */
public void setLayoutSetting(int newVal) {
	store.setValue(PREFERENCE_LAYOUT, newVal);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setPaletteSize(int)
 */
public void setPaletteSize(int newSize) {
	store.setValue(PREFERENCE_PALETTE_SIZE, newSize);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setCurrentUseLargeIcons(boolean)
 */
public void setCurrentUseLargeIcons(boolean newVal) {
	setUseLargeIcons(getLayoutSetting(), newVal);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#setUseLargeIcons(boolean)
 */
public void setUseLargeIcons(int layout, boolean newVal) {
	store.setValue(convertLayoutToPreferenceName(layout), newVal);
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#useLargeIcons()
 */
public boolean useLargeIcons(int layout) {
	return store.getBoolean(convertLayoutToPreferenceName(layout));
}

/**
 * @see org.eclipse.gef.ui.palette.PaletteViewerPreferences#useLargeIconsCurrently()
 */
public boolean useLargeIconsCurrently() {
	return useLargeIcons(getLayoutSetting());
}

private class PreferenceStoreListener implements IPropertyChangeListener {
	/**
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		String property = evt.getProperty();
		if (property.equals(PREFERENCE_LAYOUT)) {
			firePropertyChanged(property, new Integer(getLayoutSetting()));
		} else if (property.equals(PREFERENCE_AUTO_COLLAPSE)) {
			firePropertyChanged(property, new Integer(getAutoCollapseSetting()));
		} else if (property.equals(PREFERENCE_PALETTE_SIZE)) {
			firePropertyChanged(property, new Integer(getPaletteSize()));
		} else if (property.equals(PREFERENCE_FONT)) {
			firePropertyChanged(property, getFontData());
		} else {
			firePropertyChanged(property, new Boolean(
					useLargeIcons(convertPreferenceNameToLayout(property))));
		}
	}
}

}
