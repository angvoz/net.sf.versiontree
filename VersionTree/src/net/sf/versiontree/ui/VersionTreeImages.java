/*******************************************************************************
 * Copyright (c) 2012 Andrew Gvozdev and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Andrew Gvozdev <angvoz.dev@gmail.com> - initial revision
 *******************************************************************************/
package net.sf.versiontree.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.sf.versiontree.VersionTreePlugin;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * A repository of common images.
 * <p>
 * This class provides {@link Image} and {@link ImageDescriptor}
 * for each named image in the interface.  All {@code Image} objects provided
 * by this class are managed by this class and must never be disposed
 * by other clients.
 * </p>
 * <p>
 * For common platform images see {@link org.eclipse.ui.ISharedImages}
 * ({@code org.eclipse.ui.PlatformUI.getWorkbench().getSharedImages()})
 * <br>
 * and {@link org.eclipse.ui.ide.IDE.SharedImages}.
 * </p>
 */
public class VersionTreeImages {
	private static final char OVERLAY_SEPARATOR = '.';
	private static ImageRegistry imageRegistry = new ImageRegistry(getStandardDisplay());
	private static Map<String, URL> urlMap = new HashMap<String, URL>();

	// icons
	public static final String IMG_BRANCH = "icons/branch.gif";
	public static final String IMG_NA_BRANCH = "icons/na_branch.gif";
	public static final String IMG_REQUEST = "icons/tag_request.gif";
	public static final String IMG_LOCKED = "icons/tag_locked.gif";
	public static final String IMG_MERGE_TO = "icons/tag_merge_to.gif";
	public static final String IMG_MERGE_FROM = "icons/tag_merge_from.gif";
	public static final String IMG_CLOSED = "icons/tag_closed.gif";
	public static final String IMG_COMPLETED = "icons/tag_completed.gif";

	// overlays
	public static final String IMG_OVR_WARNING = "icons/ovr16/warning_co.gif";
	public static final String IMG_OVR_ERROR = "icons/ovr16/error_co.gif";

	/**
	 * Returns the standard display to be used.
	 */
	public static Display getStandardDisplay() {
		Display display= Display.getCurrent();
		if (display == null) {
			display= Display.getDefault();
		}
		return display;
	}

	/**
	 * The method finds URL of the image corresponding to the key which could be project-relative path
	 * of the image in org.eclipse.cdt.ui plugin or a (previously registered) string representation of URL
	 * in a bundle.
	 * For project-relative paths a check on existence and variables expansion (such as "$NL$")
	 * is done using {@link FileLocator}.
	 *
	 * @param key - the key which could be project-relative path of the image in org.eclipse.cdt.ui plugin
	 *     or a previously registered string representation of URL in a bundle.
	 * @return the URL or {@code null} if image was not found.
	 */
	private static URL getUrl(String key) {
		// Note that the map can keep null URL in order not to search again
		if (urlMap.containsKey(key)) {
			return urlMap.get(key);
		}

		IPath projectRelativePath = new Path(key);
		URL url = FileLocator.find(VersionTreePlugin.getDefault().getBundle(), projectRelativePath, null);
		if (url==null) {
			String msg = "Image " + key + " is missing in plugin " + VersionTreePlugin.PLUGIN_ID;
			VersionTreePlugin.log(IStatus.ERROR, msg);
		}
		urlMap.put(key, url);
		return url;
	}

	/**
	 * Internal method. It lets register image URL from a bundle directly to the map.
	 * It is user responsibility to ensure that a valid URL is passed.
	 *
	 * @param url - URL of the image pointing to its location in a bundle (bundle entry).
	 *
	 * @noreference This is internal method which is not intended to be referenced by clients.
	 */
	public static void register(URL url) {
		urlMap.put(url.toString(), url);
	}

	/**
	 * The method retrieves an image from the internal repository according to the given key.
	 * The image is managed by image registry and the caller must not dispose it.
	 *
	 * @param key - one of {@code CDTSharedImages.IMG_} constants.
	 * <p>
	 * Reserved for internal usage: the key could be a string representation of URL pointing to location
	 * of the image in the bundle. Such URL key must be registered first with {@code register(URL url)}.
	 * </p>
	 * @return the image from the repository or the default image for missing image descriptor.
	 */
	public static Image getImage(String key) {
		URL url = getUrl(key);
		String registryKey = url!=null ? url.toString() : null;
		Image image = imageRegistry.get(registryKey);
		if (image==null) {
			ImageDescriptor descriptor= ImageDescriptor.createFromURL(url);
			imageRegistry.put(registryKey, descriptor);
			image = imageRegistry.get(registryKey);
		}

		return image;
	}

	/**
	 * The method retrieves an image descriptor from the internal repository according to the given key.
	 * See also {@link #getImage(String)}.
	 *
	 * @param key - one of {@code CDTSharedImages.IMG_} constants.
	 * @return the image from the repository or {@link ImageDescriptor#getMissingImageDescriptor()}.
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		URL url = getUrl(key);
		String registryKey = url!=null ? url.toString() : null;
		ImageDescriptor descriptor = imageRegistry.getDescriptor(registryKey);
		if (descriptor==null) {
			descriptor = ImageDescriptor.createFromURL(url);
			imageRegistry.put(registryKey, descriptor);
		}
		return descriptor;
	}

	/**
	 * Retrieves an overlaid image from the internal repository of images.
	 * If there is no image one will be created.
	 *
	 * The decoration overlay for the base image will use the array of
	 * provided overlays. The indices of the array correspond to the values
	 * of the 5 overlay constants defined on {@link IDecoration}, i.e.
	 * {@link IDecoration#TOP_LEFT},
	 * {@link IDecoration#TOP_RIGHT},
	 * {@link IDecoration#BOTTOM_LEFT},
	 * {@link IDecoration#BOTTOM_RIGHT} or
	 * {@link IDecoration#UNDERLAY}.
	 *
	 * @param baseKey the base image key.
	 * @param overlayKeys the keys for the overlay images. Must be
	 *    String[5], i.e. string array of 5 elements. Put {@code null} as
	 *    an element to the array if no overlay should be added in given quadrant.
	 */
	public static Image getImageOverlaid(String baseKey, String[] overlayKeys) {
		Assert.isTrue(overlayKeys.length==5);

		String suffix="";
		for (int i=0;i<5;i++) {
			String overlayKey="";
			if (i<overlayKeys.length && overlayKeys[i]!=null) {
				overlayKey=overlayKeys[i];
			}
			suffix=suffix+OVERLAY_SEPARATOR+overlayKey;
		}
		if (suffix.length()==5) {
			// No overlays added
			Image result = getImage(baseKey);
			return result;
		}
		String compositeKey=baseKey+suffix;

		Image result = imageRegistry.get(compositeKey);
		if (result != null) {
			return result;
		}

		Image baseImage = getImage(baseKey);
		ImageDescriptor[] overlayDescriptors = new ImageDescriptor[5];
		for (int i=0;i<5;i++) {
			String overlayKey = overlayKeys[i];
			if (overlayKey!=null) {
				overlayDescriptors[i] = getImageDescriptor(overlayKey);
			}
		}
		ImageDescriptor compositeDescriptor = new DecorationOverlayIcon(baseImage, overlayDescriptors);
		imageRegistry.put(compositeKey, compositeDescriptor);
		result = imageRegistry.get(compositeKey);
		return result;
	}

	/**
	 * Retrieves an overlaid image descriptor from the repository of images.
	 * If there is no image one will be created.
	 *
	 * @param baseKey - key of the base image. Expected to be in repository.
	 * @param overlayKey - key of overlay image. Expected to be in repository as well.
	 * @param quadrant - location of overlay, one of those:
	 *        {@link IDecoration#TOP_LEFT},
	 *        {@link IDecoration#TOP_RIGHT},
	 *        {@link IDecoration#BOTTOM_LEFT},
	 *        {@link IDecoration#BOTTOM_RIGHT}
	 *
	 * @return image overlaid with smaller image in the specified quadrant.
	 */
	public static Image getImageOverlaid(String baseKey, String overlayKey, int quadrant) {
		String[] overlayKeys = new String[5];
		overlayKeys[quadrant]=overlayKey;
		return getImageOverlaid(baseKey, overlayKeys);
	}

	/**
	 * Helper method to return an image with warning overlay.
	 *
	 * @param baseKey - key of the base image. Expected to be in repository.
	 * @return an image with warning overlay.
	 */
	public static Image getImageWithWarning(String baseKey) {
		return VersionTreeImages.getImageOverlaid(baseKey, VersionTreeImages.IMG_OVR_WARNING, IDecoration.BOTTOM_LEFT);
	}

	/**
	 * Helper method to return an image with error overlay.
	 *
	 * @param baseKey - key of the base image. Expected to be in repository.
	 * @return an image with error overlay.
	 */
	public static Image getImageWithError(String baseKey) {
		return VersionTreeImages.getImageOverlaid(baseKey, VersionTreeImages.IMG_OVR_ERROR, IDecoration.BOTTOM_LEFT);
	}
}

