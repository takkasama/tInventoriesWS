window.onload = function () {
    window.ui = SwaggerUIBundle({
        url: "../ws/openapi.json",
        dom_id: "#swagger-ui",

        deepLinking: true,

        docExpansion: "list",
        defaultModelsExpandDepth: -1,
        defaultModelExpandDepth: 1,
        defaultModelRendering: "example",

        displayRequestDuration: true,
        displayOperationId: false,

        filter: true,
        tagsSorter: "alpha",
        operationsSorter: "method",

        persistAuthorization: true,

        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],

        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],

        layout: "StandaloneLayout"
    });
};
