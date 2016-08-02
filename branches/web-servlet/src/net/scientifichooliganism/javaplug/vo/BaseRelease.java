package net.scientifichooliganism.javaplug.vo;

import net.scientifichooliganism.javaplug.interfaces.Release;

public class BaseRelease extends BaseValueObject implements Release{
    private String application;
    private String name;
    private String description;
    private Date dueDate;
    private Date releaseDate;

	public BaseRelease() {
		super();
	}

	public static void main (String [] args) {
		try {
			//
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}

    String getApplication() {
        return application;
    }

    void setApplication(String in) {
        application = in;
    }

    String getName() {
        return name;
    }

    void setName(String in) {
        name = in;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String in) {
        description = in;
    }

    Date getDueDate() {
        return dueDate;
    }

    void setDueDate(Date in) {
        dueDate = in;
    }

    Date getReleaseDate() {
        return releaseDate;
    }

    void setReleaseDate(Date in) {
        releaseDate = in;
    }
}