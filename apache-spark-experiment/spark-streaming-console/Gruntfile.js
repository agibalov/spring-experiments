module.exports = function(grunt) {
    grunt.initConfig({
        srcDir: 'src/main/web',
        bowerComponentsDir: 'bower_components',
        distDir: 'src/main/resources/resources',

        amalgamatedJsFile: '<%= distDir %>/console.js',
        uglify: {
            all: {
                src: [
                    '<%= bowerComponentsDir %>/angular/angular.min.js',
                    '<%= srcDir %>/**/*.js'
                ],
                dest: '<%= amalgamatedJsFile %>'
            }
        },

        amalgamatedCssFile: '<%= distDir %>/console.css',
        cssmin: {
            all: {
                files: {
                    '<%= amalgamatedCssFile %>': [
                        '<%= bowerComponentsDir %>/bootstrap/dist/css/bootstrap.min.css',
                        '<%= bowerComponentsDir %>/angular/angular-csp.css'
                    ]
                }
            }
        },

        copy: {
            indexHtml: {
                expand: true,
                flatten: true,
                src: '<%= srcDir %>/index.html',
                dest: '<%= distDir %>'
            }
        },

        clean: [ '<%= distDir %>' ]
    });

    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-contrib-cssmin');
    grunt.registerTask('build', ['clean', 'copy', 'uglify', 'cssmin']);
};
