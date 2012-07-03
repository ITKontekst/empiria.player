package eu.ydp.empiria.player.client.module.media;

import java.util.HashMap;
import java.util.Map;

public class BaseMediaConfiguration {
	public enum MediaType {
		AUDIO, VIDEO
	}

	Map<String, String> sources = new HashMap<String, String>();
	MediaType mediaType = MediaType.AUDIO;
	private int width;
	private int height;
	private String poster;

	public BaseMediaConfiguration(Map<String, String> sources, MediaType mediaType, String poster, int height, int width) {
		super();
		this.sources = sources;
		this.mediaType = mediaType;
		this.poster = poster;
		this.height = height;
		this.width = width;
	}

	public Map<String, String> getSources() {
		return sources;
	}

	public MediaType getMediaType() {
		return mediaType;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getPoster() {
		return poster;
	}

}
