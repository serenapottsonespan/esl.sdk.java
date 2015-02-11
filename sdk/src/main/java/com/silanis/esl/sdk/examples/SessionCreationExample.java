package com.silanis.esl.sdk.examples;

import com.silanis.esl.sdk.DocumentPackage;
import com.silanis.esl.sdk.DocumentType;
import com.silanis.esl.sdk.SessionToken;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static com.silanis.esl.sdk.builder.DocumentBuilder.newDocumentWithName;
import static com.silanis.esl.sdk.builder.PackageBuilder.newPackageNamed;
import static com.silanis.esl.sdk.builder.SignatureBuilder.signatureFor;
import static com.silanis.esl.sdk.builder.SignerBuilder.newSignerWithEmail;
/**
 * 
 * Create a session token based on the package ID and the signer's ID
 *
 */
public class SessionCreationExample extends SDKSample {

    private String email1;
    private InputStream documentInputStream1;
    private String webpageUrl;
    private String signerId = "myCustomSignerId";

    public SessionToken signerSessionToken;

    public static void main( String... args ) {
        new SessionCreationExample( Props.get() ).run();
    }

    public SessionCreationExample( Properties properties ) {
        this( properties.getProperty( "api.key" ),
                properties.getProperty( "api.url" ),
                properties.getProperty( "webpage.url" ),
                properties.getProperty( "1.email" ) );
    }

    public SessionCreationExample( String apiKey, String apiUrl, String webpageUrl, String email1 ) {
        super( apiKey, apiUrl );
        this.email1 = email1;
        documentInputStream1 = this.getClass().getClassLoader().getResourceAsStream( "document.pdf" );
        this.webpageUrl = webpageUrl;
    }

    @Override
    public void execute() {
        DocumentPackage superDuperPackage = newPackageNamed( "SessionCreationExample: " + new SimpleDateFormat( "HH:mm:ss" ).format( new Date() ) )
                .withSigner(newSignerWithEmail(email1)
                        .withFirstName( "John" )
                        .withLastName( "Smith" )
                        .withCustomId( signerId ) )
                .withDocument( newDocumentWithName( "First Document" )
                        .fromStream( documentInputStream1, DocumentType.PDF )
                        .withSignature(signatureFor(email1)
                                .onPage( 0 )
                                .atPosition( 100, 100 ) ) )
                .build();

        packageId = eslClient.createPackage( superDuperPackage );
        eslClient.sendPackage( packageId );
        signerSessionToken = eslClient.createSessionToken( packageId, signerId );
        System.out.format("%s/access?sessionToken=%s%n", webpageUrl, signerSessionToken.getSessionToken());
    }
}
