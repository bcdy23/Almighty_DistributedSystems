/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import settings.CSettingManager;

/**
 *
 * @author bcho002
 */
public class CMain {

    public static void main(String... pAryArgs) {

        System.out.println(CSettingManager.getSetting("Welcome_Message"));
        
    }

}
