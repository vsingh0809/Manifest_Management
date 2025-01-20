using System.Configuration;
using System.Reflection;
using System.Text;
using ManifestPortal1.Business.Class;
using ManifestPortal1.Business.Contracts;
using ManifestPortal1.DataAccess.Helper;
using ManifestPortal1.DataAccess.Repository.Classes;
using ManifestPortal1.DataAccess.Repository.Contracts;
using ManifestPortal1.Entities.EFCore;
using ManifestPortal1.Filters;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.IdentityModel.Tokens;
using Microsoft.OpenApi.Models;
using MySql.EntityFrameworkCore;
using Serilog;

var builder = WebApplication.CreateBuilder(args);

//serilog
//Log.Logger = new LoggerConfiguration()
//                .ReadFrom.Configuration(builder.Configuration)
//                .CreateLogger();

//// Use Serilog for logging during the startup phase
//builder.Host.UseSerilog();

builder.Host.UseSerilog((context, services, configuration) =>
{
    configuration.ReadFrom.Configuration(context.Configuration) // Read from appsettings.json
                 .Enrich.FromLogContext()  // Enrich logs with contextual info
                 .WriteTo.File(
                     path: "Logs/log-.log",  // Path to log file
                     rollingInterval: RollingInterval.Day,  // Create a new log file every day
                     retainedFileCountLimit: 7  // Retain only 7 files
                 )  // Log in simple text format
                 .WriteTo.File(
                     path: "json-logs/log-.json",  // Path to JSON log file
                     rollingInterval: RollingInterval.Day,  // Create a new JSON log file every day
                     retainedFileCountLimit: 7  // Retain only 7 JSON files
                 );
});


// Add services to the container.
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "ManifestPortal API", Version = "v1" });

    // Include XML comments for swagger documentation
    var xmlFile = $"{Assembly.GetExecutingAssembly().GetName().Name}.xml";
    var xmlPath = Path.Combine(AppContext.BaseDirectory, xmlFile);
    c.IncludeXmlComments(xmlPath);
});

// Add Action Filters (Global Logging)
builder.Services.AddScoped<LoggingActionFilter>();

builder.Services.AddControllers(options =>
{
    options.Filters.Add<LoggingActionFilter>();  // Adds LoggingActionFilter globally
});

//Add controllers with JsonOptions for serialization
//builder.Services.AddControllers().AddJsonOptions(options =>
//{
//    options.JsonSerializerOptions.ReferenceHandler = System.Text.Json.Serialization.ReferenceHandler.IgnoreCycles;
//});


//Add memory cache service
builder.Services.AddMemoryCache();

// JWT Authentication Configuration
builder.Services.AddSingleton<JwtConfig>();
builder.Services.AddAuthentication(options =>
{
    options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
    options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
})
.AddJwtBearer(options =>
{
    options.TokenValidationParameters = new TokenValidationParameters
    {
        ValidateIssuer = true,
        ValidateAudience = true,
        ValidateLifetime = true,
        ValidIssuer = builder.Configuration["JwtSettings:Issuer"],
        ValidAudience = builder.Configuration["JwtSettings:Audience"],
        IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(builder.Configuration["JwtSettings:SecretKey"]))
    };
});


//Configure Entity Framework and MySQL database context
builder.Services.AddDbContext<TeamAContext>(options =>
options.UseMySQL(builder.Configuration.GetConnectionString("DefaultConnection")));

//Register repositories and services
builder.Services.AddScoped<IGuestRepository, GuestRepository>();
builder.Services.AddScoped<ICrewRepository, CrewoperatorRepo>();
builder.Services.AddScoped<IGuestService, GuestService>();
builder.Services.AddScoped<ICrewService, CrewService>();
builder.Services.AddControllers();

//Build the application
var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// Add request logging middleware with Serilog (Logging all incoming HTTP requests)
app.UseSerilogRequestLogging(options =>
{
    options.EnrichDiagnosticContext = (diagnosticContext, httpContext) =>
    {
        diagnosticContext.Set("RequestHost", httpContext.Request.Host.Value);
        diagnosticContext.Set("RequestScheme", httpContext.Request.Scheme);
    };
});

//app.UseHttpsRedirection();
//app.UseAuthentication();
app.UseAuthorization();
app.MapControllers();

// Add request logging middleware with Serilog
//app.UseSerilogRequestLogging();


try
{
    Log.Information("Starting web host");
    app.Run();//start the web server
}
catch (Exception ex)
{
    Log.Fatal(ex, "Host terminated unexpectedly");
}
finally
{
    Log.CloseAndFlush();
}


