@ IRemote Viewer written by Morten Striboldt

Using KSOAP2 with minor additions in terms of:

- Allow LK's untrusted certificate
- Added support for cookie (Session security)
- Optimzed (speed improvements)


Remember to add the following configuration to pom.xml in ksoap2-j2se:

		<dependency>
                	<groupId>org.apache.httpcomponents</groupId>
                        <artifactId>httpclient</artifactId>
                        <version>4.0-alpha4</version>
                </dependency>

this adds support for AllowAllHostnameVerifier class (used for allowing broken certifikates).

Build (Run as -> Maven install ) ksoap2-j2se, ksoap2-base, ksoap2-android and ksoap2-android-assembly and add the compiled target from ksoap2-android-assembly (ksoap2-android-assembly-2.5.2-jar-with-dependencies.jar) to
IRemote libs folder. Right click ksoap2-android-assembly-2.5.2-jar-with-dependencies.jar and select Build Path and add jar to build path.

