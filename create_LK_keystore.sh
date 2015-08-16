export CLASSPATH=bcprov-jdk14-152.jar
CERTSTORE=IRemote-Viewer/src/main/res/raw/lkstore.bks
if [ -a $CERTSTORE ]; then
    rm $CERTSTORE || exit 1
fi
keytool \
      -import \
      -v \
      -trustcacerts \
      -alias 0 \
      -file mycert.pem \
      -keystore $CERTSTORE \
      -storetype BKS \
      -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
      -providerpath bcprov-jdk14-152.jar
