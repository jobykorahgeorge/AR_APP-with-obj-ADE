# IntelliScan APK

## Deployment
Download the APK file and install on any mobile device.

## Configurations
The APK requires few configurations for it to work. To start with the configuration, after installing APK, open the application and you'll see "settings wheel icon", click on it.

![Settings icon](https://git.corp.adobe.com/coe/IntelliScan/blob/master/apk/images/config.png)

Next screen gives options to configure following:

![Default configs](https://git.corp.adobe.com/coe/IntelliScan/blob/master/apk/images/config1.png)

* **AEM publish server address** - Server address where AR content is stored. It can be the hostname of the server but should be only till port number. For accessing AWS server, make sure the mobile device is connected to **Adobe Guest network**.
* **Relative path of the JSON file** for the two products:
  * Activity tracker 
  * Shoes 
* Ability to choose **scan mode**:
  * AR Scanner - Works with colors BLUE and RED
  * Barcode Scanner   - Works with configured barcodes
  
    ![Barcode configs](https://git.corp.adobe.com/coe/IntelliScan/blob/master/apk/images/barcode_config.png)
    * If barcode is selected then further configuration screen is loaded which 

### Default Configurations

* **App Server URL** - http://aem.aws726.adobeitc.com:4503
* **Product One Path** - /content/dam/ar_app/56789.json
* **Product Two Path** - /content/dam/ar_app/12345.json
* If barcode scan mode is selected then provide barcode (can be anything in the following configurations, but below are already printed and kept with rest of the props for the demo:
  * **Product One Barcode** - 445566
  * **Product One Barcode** - 112233
* Click on file save icon to save the changes.

After the configuration, you are good to go with the demo. In case of AR Scanner mode, scan color Blue and Red props to get the required details on the screen. In case of of barcode, scan the respective barcodes to get the details. 

In case, you plan to use different barcodes, you visit https://barcode.tec-it.com/en/Code128 to generate and print new barcode. 

**NOTE: Please do make sure the barcode/color matchs respective products mentioned in the product URLs**

