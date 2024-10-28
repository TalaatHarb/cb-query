package net.talaatharb.query;

import javafx.application.Application;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CBQueryApplication{
	public static void main(String[] args) {
		log.info("UI Application Starting");
		Application.launch(JavafxApplication.class, args);
	}
}