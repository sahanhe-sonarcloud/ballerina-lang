# Ballerina Language Server

Welcome to the Ballerina Language Server documentation. This section contains,

1. How to write extended features on top of the language server (like extended language server services and compiler
   plugin code actions)
2. Explanations to various designs used inside the language server (developer documentation)
3. Best practices that should be followed when implementing language server features

These documentations are intended for the developers who wish to understand the internals of the Ballerina language
server implementation and for developers who are willing to write new language server features.

## Features
* [Completions and Code Actions](Features.md)

## Document Event Sync PubSub Model
A mechanism to get notified on project update, document change and other interested events across the Language Server.

* [Design](DocumentEventSyncPublisherSubscriberDesign.md)
* [Developer Guide](DocumentEventSyncPublisherSubscriberDeveloperGuide.md)

## Extended Language Server Services
* [Writing Extended Services for Language Server](WritingExtendedServices.md)
* [Extension Development Best Practices](ExtensionDevelopmentBestPractices.md)

## Compiler Plugins
* [Compiler Plugin Code Actions](CompilerPluginCodeActions.md)

## Other Resources
* [LSP, Ballerina Language Server and Blogs](Resources.md)
