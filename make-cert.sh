keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -keypass mykeypassword -storepass mykeystorepassword -validity 360 -keysize 2048 -dname "CN=loki2302-cn, O=loki2302-o, OU=loki2302-ou, L=loki2302-l, ST=loki2302-st, C=loki2302-c"
mv keystore.jks src/main/resources
